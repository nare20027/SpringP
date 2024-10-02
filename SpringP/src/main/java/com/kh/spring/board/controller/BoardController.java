package com.kh.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.GsonBuilder;
import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.PageInfo;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.Pagination;
import com.kh.spring.common.exception.CommException;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@RequestMapping("list.bo")
	public String selectList(@RequestParam(value="currentPage", required = false, defaultValue = "1") int currentPage, Model model) {
		/* 1. @RequestParam(value="currentPage") int currentPage --> 값이 넘어오지 않았을 때 에러(값을 주입할 수 없어서)
		 * 	
		 * 2. @RequestParam(value="currentPage", required = false) int currentPage
		 * --> required : 해당 파라미터의 필수 여부 (default-> true :필수) , Autowird 에서도 사용
		 * 	    따라서 required = false 로 지정하여 값을 반드시 받을 필요가 없다고 선언 (null이 들어올 수 있다.)
		 * 	  :> null 은 기본형 int에 들어갈 수 없기 때문에 에러 발생 
		 * 3. @RequestParam(value="currentPage", required = false, defaultValue = "1")
		 * 	  defaultValue : 넘어오는 값이 null일 경우 해당 파라미터의 기본값을 지정할 수 있다. 
		 * */
		
		int listCount = boardService.selectListCount();
		System.out.println(listCount);
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, 10, 5); // pageLimit : 10, boardLimit : 5
		
		ArrayList<Board> list = boardService.selectList(pi);
		System.out.println(list);
		
		model.addAttribute("list",list);
		model.addAttribute("pi",pi);
		
		return "board/boardListView";
	}
	
	@RequestMapping("enrollForm.bo")
	public String enrollForm() {
		return "board/boardEnrollForm";
	}
	
	@RequestMapping("insert.bo")
	public String insertBoard(Board b, HttpServletRequest request, Model model, 
				@RequestParam(name="uploadFile", required=false) MultipartFile file) { 
				//@RequestParam(name="uploadFile", required=false) MultipartFile file : 첨부파일을 받는 방법 
		
		System.out.println(b);
		System.out.println(file.getOriginalFilename());
		
		if(!file.getOriginalFilename().equals("")) { // 파일이름이 빈 문자열이 아닐 때 == 첨부파일이 있을 때 
			String changeName = saveFile(file, request);
			
			if(changeName != null) { // 파일 업로드 성공 
				b.setOriginName(file.getOriginalFilename());
				b.setChangeName(changeName);
			}
		}
		
		boardService.insertBoard(b);

		
		return "redirect:list.bo";
	}

	// 파일 이름 변경 후 업로드하기 위한 메소드 
	private String saveFile(MultipartFile file, HttpServletRequest request) {
		
		String resources = request.getSession().getServletContext().getRealPath("resources");
		String savePath = resources + "\\upload_files\\";
		
		System.out.println("savePath : " + savePath);
		
		String originName = file.getOriginalFilename();
		
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		String ext = originName.substring(originName.lastIndexOf(".")); // 확장자 가져오기 
		
		String changeName = currentTime + ext;
		
		try { // transferTo -> rename 된 파일명으로 업로드 
			file.transferTo(new File(savePath + changeName));
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CommException("file upload error");
		}
		
		return changeName; // 변경된 이름 return 
	}
	
	// 게시글 상세 조회 
	@RequestMapping("detail.bo")
	public ModelAndView selectBoard(int bno, ModelAndView mv) {
		
		Board b = boardService.selectBoard(bno);
		
		mv.addObject("b", b).setViewName("board/boardDetailView");
		
		return mv;
		
	}
	
	// 게시글 삭제
	@RequestMapping("delete.bo")
	public String deleteBoard(int bno, String fileName, HttpServletRequest request) {
		
		boardService.deleteBoard(bno);
		
		if(!fileName.equals("")) { // 첨부파일이 있을 때
			deleteFile(fileName, request); // 게시글 수정 시에도 필요하므로 메소드를 따로 빼서 사용
		}
		
		return "redirect:list.bo";
		
	}

	// 첨부파일 삭제 
	private void deleteFile(String fileName, HttpServletRequest request) {
		String resources = request.getSession().getServletContext().getRealPath("resources");
		String savePath = resources + "\\upload_files\\";
		
		System.out.println("savePath : " + savePath);
		
		File deleteFile = new File(savePath + fileName);
		
		deleteFile.delete(); // 파일 바로 삭제 
		
	}
	
	@RequestMapping("updateForm.bo")
	public ModelAndView updateForm(int bno, ModelAndView mv) {
		
		Board b = boardService.selectBoard(bno);
		
		mv.addObject("b", b).setViewName("board/boardUpdateForm");
		
		return mv;
		
	}
	
	//게시글 수정
	@RequestMapping("update.bo")
	public ModelAndView updateBoard(Board b, ModelAndView mv, HttpServletRequest request,
									@RequestParam(value="reUploadFile",required=false) MultipartFile file) {
		
		if(!file.getOriginalFilename().equals("")) { //새로 등록된 파일이 있을 때 
			
			if(b.getChangeName() != null) { // 원래 등록된 첨부파일이 있을 때 
				deleteFile(b.getChangeName(), request);
			}
			
			String changeName = saveFile(file,request);
			
			b.setOriginName(file.getOriginalFilename());
			b.setChangeName(changeName);
		}
		
		/*
		 * 1. 기존의 첨부파일 X, 새로 첨부된 파일 X 	
		 * 	  --> originName : null, changeName : null
		 * 
		 * 2. 기존의 첨부파일 X, 새로 첨부된 파일 O		
		 * 	  --> 서버에 업로드 후 
		 * 	  --> originName : 새로첨부된파일원본명, changeName : 새로첨부된파일수정명
		 * 
		 * 3. 기존의 첨부파일 O, 새로 첨부된 파일 X		
		 * 	  --> originName : 기존첨부파일원본명, changeName : 기존첨부파일수정명
		 * 
		 * 4. 기존의 첨부파일 O, 새로 첨부된 파일 O  
		 * 	  --> 서버에 업로드 후	
		 * 	  --> originName : 새로첨부된파일원본명, changeName : 새로첨부된파일수정명
		 */
		
		boardService.updateBoard(b);
		
		mv.addObject("bno",b.getBoardNo()).setViewName("redirect:detail.bo");
		
		return mv;
		
	}
	
	// 댓글 조회 
	@ResponseBody // 화면단에서 비동기로 처리하고 객체 타입 그대로를 받을 수 있다. 
	@RequestMapping(value = "rlist.bo", produces="application/json; charset=UTF8") //ajax로 받아 json으로 리턴하기 위해 작성 
	public String selectReplyList(int bno) {
		
		ArrayList<Reply> list = boardService.selectReplyList(bno);
		
		System.out.println("replylist : " + list.get(0).getCreateDate().toString());
		
		return new GsonBuilder().setDateFormat("yyyy년 MM월 dd일 HH:mm:ss").create().toJson(list); // Gson 사용, json 추가설정 필요
		
	}
	
	@ResponseBody
	@RequestMapping("rinsert.bo")
	public String insertReply(Reply r) { // 파라미터 명과 vo vo변수명이 같으므로 객체로 바로 받을 수 있다.
		
		int result = boardService.insertReply(r);
		
		return String.valueOf(result);
		
	}
	
	//top list
	@ResponseBody // 화면단에서 비동기로 처리하고 객체 타입 그대로를 받을 수 있다. 
	@RequestMapping(value = "topList.bo", produces="application/json; charset=UTF8") //ajax로 받아 json으로 리턴하기 위해 작성 
	public String selectTopList() {
		
		ArrayList<Board> list = boardService.selectTopList();
		
		System.out.println("replylist : " + list.get(0).getCreateDate().toString());
		
		return new GsonBuilder().setDateFormat("yyyy년 MM월 dd일 HH:mm:ss").create().toJson(list); // Gson 사용, json 추가설정 필요
		
	}
}

