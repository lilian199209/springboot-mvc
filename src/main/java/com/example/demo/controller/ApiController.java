package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.BMI;
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

}