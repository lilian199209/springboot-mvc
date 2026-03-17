package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BookController
 * ============================================
 * 📘 功能說明：
 * 本類別是「書籍管理系統」的 RESTful API 控制器，
 * 負責接收前端 (例如 React / Vue) 的 HTTP 請求，
 * 並呼叫 Service 層進行商業邏輯處理，最後回傳 JSON 結果。
 *
 * --------------------------------------------
 * 📌 使用技術：
 * - Spring Boot (REST API)
 * - ResponseEntity (控制 HTTP 狀態碼)
 * - ApiResponse (統一回傳格式)
 *
 * --------------------------------------------
 * 📌 RESTful API 設計概念：
 *
 * 方法        路徑            功能
 * --------------------------------------------
 * GET         /book          查詢全部書籍
 * GET         /book/{id}     查詢單一書籍
 * POST        /book          新增書籍
 * PUT         /book/{id}     更新整本書 (完整更新)
 * PATCH       /book/{id}     部分更新 (name + price)
 * PATCH       /book/name/{id}   只改名稱
 * PATCH       /book/price/{id}  只改價格
 * DELETE      /book/{id}     刪除書籍
 *
 * --------------------------------------------
 * 📌 分層架構 (MVC)：
 *
 * Controller → 接收請求 (HTTP)
 * Service    → 商業邏輯
 * Repository → 資料庫操作
 *
 * --------------------------------------------
 * 📌 設計重點：
 *
 * 1️. 使用 ResponseEntity
 *   - 可控制 HTTP Status (200, 400, 404...)
 *
 * 2️. 使用 ApiResponse
 *   - 統一 JSON 回傳格式
 *   {
 *     "message": "...",
 *     "data": ...
 *   }
 *
 * 3️. 例外處理 (Exception Handling)
 *   - 捕捉 BookException
 *   - 避免系統直接 crash
 *
 * 4️. RESTful 設計
 *   - 使用 HTTP Method 表達行為
 *   - URL 表示資源 (book)
 *
 * --------------------------------------------
 * 📌 前端呼叫範例：
 *
 * GET    http://localhost:8080/book
 * POST   http://localhost:8080/book
 * PUT    http://localhost:8080/book/1
 * PATCH  http://localhost:8080/book/price/1
 * DELETE http://localhost:8080/book/1
 *
 * --------------------------------------------
 * 📌 跨域設定：
 * @CrossOrigin 允許前端 (localhost:5173) 呼叫 API
 * (常見於 React / Vite 開發環境)
 *
 * ============================================
 */
@CrossOrigin(origins = {"http://localhost:5173"}) // 允許 跨域請求 給 react 使用
@RestController
@RequestMapping("/book")
public class BookController {
	

}
