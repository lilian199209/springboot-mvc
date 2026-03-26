// ========================
// 功能:
// 1.查詢全部
// 2.查詢單筆
// 3.顯示訊息
// 4.顯示表格
// ========================

// API 基本路徑
const API_BASE_URL = "/book";

// 抓取畫面元素(id) - 查詢相關
const messageBox = document.getElementById("messageBox");
const searchId = document.getElementById("searchId");
const findOneBtn = document.getElementById("findOneBtn");
const findAllBtn = document.getElementById("findAllBtn");
const singleResult = document.getElementById("singleResult");
const bookTableBody = document.getElementById("bookTableBody");

// 抓取畫面元素(id) - 新增表單相關
const bookForm = document.getElementById("bookForm");
const bookId = document.getElementById("bookId");
const bookName = document.getElementById("bookName");
const bookPrice = document.getElementById("bookPrice");
const bookAmount = document.getElementById("bookAmount");
const bookPub = document.getElementById("bookPub");
const addBtn = document.getElementById("addBtn");
const updateBtn = document.getElementById("updateBtn");
const resetBtn = document.getElementById("resetBtn");

// 紀錄目前表單模式
// create = 新增模式
// update = 修改模式
let formMode = "create";

// 綁定查詢按鈕事件
findOneBtn.addEventListener("click", findBookById);
findAllBtn.addEventListener("click", findAllBooks);
addBtn.addEventListener("click", addBook);
updateBtn.addEventListener("click", updateBook);
resetBtn.addEventListener("click", () => {
	bookForm.reset();
	updateFormMode("create");
});

// 頁面載入初始化
window.addEventListener("DOMContentLoaded", () => {
	findAllBooks();
	updateFormMode("create");
})

// 顯示訊息
function showMessage(message, type = "info") {
	messageBox.textContent = message;
	messageBox.className = `message-box ${type}`;
}

// 統一控制模式的方法
function updateFormMode(mode) {
	formMode = mode;
	
	if(mode === "create") {
		addBtn.disabled = false;
		updateBtn.disabled = true;
	} else {
		addBtn.disabled = true;
		updateBtn.disabled = false;
	}
}

// 統一處理 fetch 回應
async function handleResponse(response) {
	const result = await response.json();
	
	if(!response.ok) {
		throw new Error(result.message || "發生錯誤");
	}
	
	return result;
}

// 將資料帶回表單
function fillForm(book) {
	bookId.value = book.id ?? "";
	bookName.value = book.name ?? "";
	bookPrice.value = book.price ?? "";
	bookAmount.value = book.amount ?? "";
	bookPub.checked = book.pub ?? false;
	
	updateFormMode("update");
}

// 表單新增的資料
function getBookFormDataForCreate() {
	return {
		name: bookName.value.trim(),
		price: bookPrice.value ? Number(bookPrice.value) : null,
		amount: bookAmount.value ? Number(bookAmount.value) : null,
		pub: bookPub.checked
	};
}

// 表單修改的資料
function getBookFormDataForUpdate() {
	return {
		id: bookId.value ? Number(bookId.value) : null,
		name: bookName.value.trim(),
		price: bookPrice.value ? Number(bookPrice.value) : null,
		amount: bookAmount.value ? Number(bookAmount.value) : null,
		pub: bookPub.checked
	};
}

// 新增書籍
async function addBook() {
	try {
		/*const book = {
			name: bookName.value.trim(),
			price: bookPrice.value ? Number(bookPrice.value) : null,
			amount: bookAmount.value ? Number(bookAmount.value) : null,
			pub: bookPub.checked
		};*/
		const book = getBookFormDataForCreate();
		
		if(!book.name || book.price === null || book.amount === null) {
			showMessage("請輸入完整書名、價格、數量", "error");
			return;
		}
		
		const reponse = await fetch(API_BASE_URL, {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(book)
		});
		
		const result = await handleResponse(reponse);
		
		console.log(result);
		showMessage(result.message || "新增成功", "success");
		
		bookForm.reset(); //清空表單
		findAllBooks(); //重新查詢所有書籍
		
	} catch(error) {
		showMessage(error.message, "error");
	}
}

// 修改書籍
async function updateBook() {
	try {
		
		const book = getBookFormDataForUpdate();
		
		if(!book.id){
			showMessage("修改時必須要有ID", "error");
			return;
		}
		
		if(!book.name || book.price === null || book.amount === null){
			showMessage("請輸入完整書名、價格、數量", "error");
			return;
		}
		
		const response = await fetch(`${API_BASE_URL}/${book.id}`, {
			method: "PUT",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(book)
		});
		
		const result = await handleResponse(response);
		
		console.log(result); //debug用
		showMessage(result.message || "修改成功", "success");
		singleResult.textContent = JSON.stringify(result.data);
		findAllBooks(); //重新查詢所有書籍
		
	} catch(error) {
		showMessage(error.message, "error");
	}
}

// 查詢全部
async function findAllBooks() {
	try {
		const response = await fetch(API_BASE_URL);
		const result = await handleResponse(response);
		console.log(result); //debug用
		renderBookTable(result.data); //資料渲染到表格中
		showMessage(result.message || "查詢全部成功", "success");
	} catch(error) {
		renderBookTable([]); //資料渲染到表格中
		showMessage(error.message, "error");
	}
}

// 查詢單筆
async function findBookById() {
	try {
		const id = searchId.value;
		
		if(!id){
			showMessage("請輸入要查詢的書籍id", "error");
			return;
		}
		
		const response = await fetch(`${API_BASE_URL}/${id}`);
		const result = await handleResponse(response);
		
		console.log(result); //debug用，顯示json物件
		console.log(JSON.stringify(result)); //debug用，顯示json字串
		
		singleResult.textContent = JSON.stringify(result);
		showMessage(result.message || "單筆查詢成功", "success");
		
		// 將查到的資料帶回表單
		fillForm(result.data);
		
	} catch(error) {
		singleResult.textContent = "查詢失敗";
		showMessage(error.message, "error");
	}
}

// 由表格載入資料到表單
async function loadBookById(id) {
	try {
		const response = await fetch(`${API_BASE_URL}/${id}`);
		const result = await handleResponse(response);
		
		singleResult.textContent = JSON.stringify(result);
		showMessage(`已載入書籍 ID: ${id}`, "success");
		
		// 將查到的資料帶回表單
		fillForm(result.data);
		
	} catch(error) {
		singleResult.textContent = `載入書籍 ID: ${id} 失敗`;
		showMessage(error.message, "error");
	}
}

// 刪除書籍
async function deleteBookById(id) {
	
	const isConfirmed = confirm(`你確定要刪除書籍 ID: ${id} 嗎?`);
	
	if(!isConfirmed) {
		showMessage(`已取消刪除書籍 ID: ${id}`, "info");
		return;
	}
	
	try {
		const response = await fetch(`${API_BASE_URL}/${id}`, {
			method: "DELETE"
		});
		
		const result = await handleResponse(response);
		
		showMessage(result.message || "刪除成功", "success");
		findAllBooks();
		
	} catch(error) {
		singleResult.textContent = `刪除書籍 ID: ${id} 失敗`;
		showMessage(error.message, "error");
	}
}

// 渲染表格
function renderBookTable(books) {
	if(!books || books.length === 0) {
		bookTableBody.innerHTML = `
			<tr>
				<td colspan="6" class="empty-now">目前沒有資料</td>
			</tr>
		`;
		return;
	}
	
	let html = "";
	books.forEach(book => {
		html += `
			<tr>
				<td onclick="loadBookById(${book.id})" style="cursor:pointer;">${book.id}</td>
				<td>${escapeHTML(book.name)}</td>
				<td>${book.price}</td>
				<td>${book.amount}</td>
				<td>${book.pub ? "是" : "否"}</td>
				
				<td>
					<button onclick="loadBookById(${book.id})">修改</button>
					<button onclick="deleteBookById(${book.id})" class="danger">刪除</button>
				</td>
			</tr>
		`;
	});
	
	bookTableBody.innerHTML = html;
}

// 避免文字直接塞進html
function escapeHTML(text) {
	if(text === null || text === undefined) {
		return "";
	}
	return String(text)
			.replaceAll("&", "&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll('"', "&quot;")
			.replaceAll("'", "&#39;");
}

