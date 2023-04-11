package kakako.login.Login;

import java.util.HashMap;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HomeController {
	
    private final KakaoAPI kakaoApi;
    
  //생성자를 통해 KakaoAPI 객체를 주입받습니다.
    public HomeController(KakaoAPI kakaoApi) {
        this.kakaoApi = kakaoApi;
    }

 // 로그인 요청 처리를 위한 메소드입니다.
    @RequestMapping(value="/login")
    public ModelAndView login(@RequestParam("code") String code, HttpServletRequest session) {
        ModelAndView mav = new ModelAndView("index");

     // 카카오 API를 이용하여 액세스 토큰과 사용자 정보를 가져옵니다.
        String accessToken = kakaoApi.getAccessToken(code);
        HashMap<String, Object> userInfo = kakaoApi.getUserInfo(accessToken);

        System.out.println("Login info : " + userInfo.toString());

     // 사용자 정보 중 이메일이 있다면, 세션에 사용자 아이디와 액세스 토큰을 저장하고,
     // ModelAndView 객체에 사용자 아이디를 추가합니다.
        if (userInfo.get("email") != null) {
            session.setAttribute("userId", userInfo.get("email"));
            session.setAttribute("accessToken", accessToken);
            mav.addObject("userId", userInfo.get("email"));
        }
        
        return mav;
    }

 // 로그아웃 요청 처리를 위한 메소드입니다.
    @RequestMapping(value="/logout")
    public ModelAndView logout(HttpServletRequest session) {
        ModelAndView mav = new ModelAndView("index");
     
     // 카카오 API를 이용하여 액세스 토큰을 폐기하고, 세션에서 사용자 아이디와 액세스 토큰을 삭제합니다.
        kakaoApi.kakaoLogout((String) session.getAttribute("accessToken"));
        session.removeAttribute("accessToken");
        session.removeAttribute("userId");

        return mav;
    }

}
