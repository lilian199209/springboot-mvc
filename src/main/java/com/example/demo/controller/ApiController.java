package com.example.demo.controller;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.BMI;
import com.example.demo.model.Book;
import com.example.demo.response.ApiResponse;

// @Controller
@RestController //可以省去撰寫 @ResponseBody
@RequestMapping("/api") //資源分組
public class ApiController {
	
	/**
	 * 1. API首頁
	 * 路徑1: /home
	 * 路徑2: /welcome
	 * 網址1: http://localhost:8080/api/home
	 * 網址2: http://localhost:8080/api/welcome
	 */
	@GetMapping(value = {"/home", "/welcome"}, produces = "text/plain;charset=utf-8")
	// @ResponseBody
	public String home() {
		return "我是API首頁";
	}
	
	/**
	 * 2. ?帶參數
	 * 路徑1: /greet?name=John&age=18
	 * 網址1: http://localhost:8080/api/greet?name=John&age=18
	 * 結果1: Hi John, 18(成年)
	 * 
	 * 路徑2: /greet?name=Mary
	 * 網址2: http://localhost:8080/api/greet?name=Mary
	 * 結果2: Hi Mary, 0(未成年)
	 * 
	 * 限制: name 為必要參數, age 為可選參數(有初始值 0)
	 */
	// @GetMapping(value = {"/greet"})
	// @GetMapping(value = "/greet")
	@GetMapping("/greet")
	public String greet(@RequestParam(value = "name", required = true) String username,
						@RequestParam(value = "age", required = false, defaultValue = "0") Integer userage) {
		String result = String.format("Hi %s, %d(%s)", username, userage, userage>=18?"成年":"未成年");
		return result;
	}
	
	/**
	 * 3. 上述 2 的精簡寫法
	 * 方法參數名稱與請求參數名稱相同
	 */
	@GetMapping("/greet2")
	public String greet2(@RequestParam String name,
						@RequestParam(required = false, defaultValue = "0") Integer age) {
//		String result = String.format("Hi %s, %d(%s)", name, age, age>=18?"成年":"未成年");
//		return result;
		return greet(name, age);
	}
	
	/**
	 * 4. 練習
	 * 路徑: /bmi?h=170&w=60
	 * 網址: http://localhost:8080/api/bmi?h=170&w=60
	 * 判斷: bmi <= 18 顯示過輕, bmi > 23 顯示過重
	 * 執行結果: 身高:170cm 體重:60kg bmi=20.76(正常)
	 */
	@GetMapping("/bmi")
	public String bmi(@RequestParam(value = "h") double height,
						@RequestParam(value = "w") double weight) {
		double bmi = weight/(height/100)/(height/100);
		System.out.println(bmi);
		String res="";
		if(bmi<=18) {
			res = "過輕";
		}else if(bmi>23) {
			res = "過重";
		}else {
			res = "正常";
		}
		String result = String.format("身高: %.1f cm 體重: %.1f kg bmi= %.2f (%s)", height, weight, bmi, res);
		return result;
	}
	
	/**
	 * 5. 回傳json結構
	 * 路徑: /json/bmi?h=170&w=60
	 * 網址: http://localhost:8080/api/json/bmi?h=170&w=60
	 * 判斷: bmi <= 18 顯示過輕, bmi > 23 顯示過重
	 * 執行結果: 
	 * {
	 *   "status": 200,
	 *   "message": "BMI 計算成功",
	 *   "data": {
	 *     "height": 170.0,
	 *     "weight": 60.0,
	 *     "bmi": 20.76
	 *   }
	 * }
	 */
	@GetMapping(value = "/json/bmi")
	public ResponseEntity<ApiResponse<BMI>> calBmi(@RequestParam(required = false, defaultValue = "0", value = "h") double height, @RequestParam(required = false, defaultValue = "0", value = "w") double weight) {
		if(height==0|| weight==0) {
			// badRequest => HTTP 400
			return ResponseEntity.badRequest().body(ApiResponse.error("請輸入身高體重參數"));
		}
		
		if(height<=0|| weight<=0) {
			// badRequest => HTTP 400
			//return ResponseEntity.badRequest().body(new ApiResponse<>("身高體重參數錯誤", null));
			return ResponseEntity.badRequest().body(ApiResponse.error("身高體重參數錯誤"));
		}
		
		double bmiValue = weight / Math.pow(height/100, 2);
		BMI bmi = new BMI(height, weight, bmiValue);
		
		// ok => HTTP 200
		//ApiResponse<BMI> apiResponse = new ApiResponse<BMI>("計算成功", bmi);
		//return ResponseEntity.ok(apiResponse);
		return ResponseEntity.ok(ApiResponse.success("計算成功", bmi));
	}
	
	/**
	 * 6. 同名多筆資料
	 * 路徑: /json/age?age=170&age=60&age=4
	 * 網址: http://localhost:8080/api/json/age?age=17&age=21&age=20
	 * 請計算出平均年齡
	 */
	@GetMapping(value = "/json/age", produces = "application/json;charset=utf-8")
	public ResponseEntity<ApiResponse<Object>> getAverage(@RequestParam(value = "age", required = false) List<Integer> ages) {
		if(ages == null || ages.size() == 0) {
			return ResponseEntity.badRequest().body(ApiResponse.error("請輸入年齡(age)"));
		}
		
		double avg = ages.stream().mapToInt(Integer::valueOf).average().orElseGet(() -> 0);
		Object data = Map.of("年齡", ages, "平均年齡", String.format("%.1f", avg));
		return ResponseEntity.ok(ApiResponse.success("計算成功", data));
	}
	
	/**
	 * 7. Lab 練習: 得到多筆 score 資料
	 * 路徑: "/json/score?score=80&score=100&score=50&score=70&score=30"
	 * 網址: http://localhost:8080/api/json/score?score=80&score=100&score=50&score=70&score=30
	 * 請自行設計一個方法，此方法可以
	 * 印出: 最高分=?、最低分=?、平均=?、總分=?、及格分數列出=?、不及格分數列出=?
	 */
	@GetMapping(value = "/json/score", produces = "application/json;charset=utf-8")
	public ResponseEntity<ApiResponse<Object>> getScoreAverage(@RequestParam(value = "score", required = false) List<Integer> scores) {
		if(scores == null || scores.size() == 0) {
			return ResponseEntity.badRequest().body(ApiResponse.error("請輸入分數(score)"));
		}
		
		double avg = scores.stream().mapToInt(Integer::valueOf).average().orElseGet(() -> 0);
		double max = scores.stream().mapToInt(Integer::valueOf).max().orElseGet(() -> 0);
		double min = scores.stream().mapToInt(Integer::valueOf).min().orElseGet(() -> 0);
		double sum = scores.stream().mapToInt(Integer::valueOf).sum();
		
		List good = new ArrayList<>();
		List bad = new ArrayList<>();
		for(Integer s:scores) {
			if(s >= 60) {
				good.add(s);
			}else {
				bad.add(s);
			}
		}
		
		Object data = Map.of("最高分", max, "最低分", min, "平均", String.format("%.1f", avg), "總分", sum, "及格分數", good, "不及格分數", bad);
		return ResponseEntity.ok(ApiResponse.success("計算成功", data));
	}
	
	/**
	 * 8. 多筆參數轉 Map
	 * name 書名(String), price 價格(Double), amount 數量(Integer), pub 出刊/停刊(Boolean)
	 * 路徑: /json/book?name=Math&price=12.5&amount=10&pub=true
	 * 路徑: /json/book?name=English&price=10.5&amount=20&pub=false
	 * 網址: http://localhost:8080/api/json/book?name=Math&price=12.5&amount=10&pub=true
	 * 網址: http://localhost:8080/api/json/book?name=English&price=10.5&amount=20&pub=false
	 * 讓參數自動轉成 key/value 的 Map 集合
	 */
	@GetMapping(value = "/json/book", produces = "application/json;charset=utf-8")
	public ResponseEntity<ApiResponse<Object>> getBookInfo(@RequestParam Map<String, Object> bookMap) {
		System.out.printf("bookMap = %s%n", bookMap);
		return ResponseEntity.ok(ApiResponse.success("成功", bookMap));
	}
	
	/**
	 * 9. 多筆參數轉 model
	 * 路徑: /json/book2?name=Math&price=12.5&amount=10&pub=true
	 * 網址: http://localhost:8080/api/json/book2?name=Math&price=12.5&amount=10&pub=true
	 */
	@GetMapping(value = "/json/book2", produces = "application/json;charset=utf-8")
	public ResponseEntity<ApiResponse<Object>> getBookInfo2(Book book) {
		book.setId(1); // 設定id
		System.out.printf("book = %s%n", book);
		return ResponseEntity.ok(ApiResponse.success("成功", book));
	}
	
	/**
	 * 10. 路徑參數
	 * 
	 * 早期設計風格
	 * 路徑: /json/book?id=1 得到 id=1 的書
	 * 路徑: /json/book?id=2 得到 id=2 的書
	 * 
	 * 現代設計風格(REST)
	 * GET /books  查詢所有書籍
	 * GET /book/1 查詢指定書籍
	 * 
	 * 路徑: /json/book/1 得到 id=1 的書
	 * 路徑: /json/book/2 得到 id=2 的書
	 * 網址: http://localhost:8080/api/json/book/1
	 * 網址: http://localhost:8080/api/json/book/2
	 */
	@GetMapping(value = "/json/book/{id}")
	//public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable(name = "id") Integer id) {
	public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable Integer id) {
		// 書庫
		List<Book> books = List.of(
				new Book(1, "多拉A夢", 12.5, 20, true),
				new Book(2, "蠟筆小新", 10.5, 30, true),
				new Book(3, "名偵探柯南", 9.5, 40, true),
				new Book(4, "多拉B夢", 14.5, 0, false));
		
		// 根據id搜尋該筆書籍
		Optional<Book> optBook = books.stream().filter(book -> book.getId().equals(id)).findFirst();
		
		// 判斷是否有找到
		if(optBook.isEmpty()) {
			return ResponseEntity.badRequest().body(ApiResponse.error("查無此書"));
		}
		
		Book book = optBook.get(); // 取得書籍物件
		return ResponseEntity.ok(ApiResponse.success("查詢成功", book));
		
	}
	
	/**
	 * 書庫: 請參考上面的實作
	 * 
	 * 得到已出版(pub=true)的書籍
	 * 網址: http://localhost:8080/api/json/book/pub/true
	 * 
	 * 得到未出版(pub=false)的書籍
	 * 網址: http://localhost:8080/api/json/book/pub/false
	 */
	@GetMapping(value = "/json/book/pub/{pub}")
	public ResponseEntity<ApiResponse<List<Book>>> getBookByPub(@PathVariable Boolean pub) {
		// 書庫
		List<Book> books = List.of(
				new Book(1, "多拉A夢", 12.5, 20, true),
				new Book(2, "蠟筆小新", 10.5, 30, true),
				new Book(3, "名偵探柯南", 9.5, 40, true),
				new Book(4, "多拉B夢", 14.5, 0, false));
		
		// 根據id搜尋該筆書籍
		List<Book> optBook = new ArrayList<Book>();
		for(Book book : books) {
			if(book.getPub().equals(pub)) {
				optBook.add(book);
			}
		}
		
		// 判斷是否有找到
		if(optBook.isEmpty()) {
			return ResponseEntity.badRequest().body(ApiResponse.error("查無資料"));
		}
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", optBook));
		
	}
	
	@GetMapping(value = "/json/book/pub2/{pub}")
	public ResponseEntity<ApiResponse<List<Book>>> getBookByPub2(@PathVariable Boolean pub) {
		// 書庫
		List<Book> books = List.of(
				new Book(1, "多拉A夢", 12.5, 20, true),
				new Book(2, "蠟筆小新", 10.5, 30, true),
				new Book(3, "名偵探柯南", 9.5, 40, true),
				new Book(4, "多拉B夢", 14.5, 0, false));
		
		// 根據id搜尋該筆書籍
		List<Book> optBook = books.stream().filter(book -> book.getPub().equals(pub)).toList();
		
		// 判斷是否有找到
		if(optBook.size() == 0) {
			return ResponseEntity.badRequest().body(ApiResponse.error("查無" + (pub?"出刊":"停刊") + "書籍資料"));
		}
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功:" + (pub?"出刊":"停刊"), optBook));
		
	}

}