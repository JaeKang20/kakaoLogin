package kakako.login.Login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class KakaoAPI {

	/*
카카오 인증코드를 이용하여 액세스 토큰을 발급 받는 메소드
	   POST /oauth/token
	   Host: kauth.kakao.com
	  */
	public String getAccessToken(String code) {
		String accessToken = "";
		String refreshToken = "";
		String reqURL = "https://kauth.kakao.com/oauth/token";
		
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			
			// 카카오에 요청할 데이터를 만듭니다.
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=authorization_code");
			sb.append("&client_id=5559c68695b9d154a339d12cac73e20a");
			sb.append("&redirect_uri=http://localhost:8080/login");
			sb.append("&code="+code);
			
			bw.write(sb.toString());
			bw.flush();
			
			int responseCode = conn.getResponseCode();
			System.out.println("response code = " + responseCode);
			
			// 카카오로부터 받은 응답 데이터를 읽습니다.
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";
			while((line = br.readLine())!=null) {
				result += line;
			}
			System.out.println("response body="+result);
			
			// 받은 응답 데이터를 Json 객체로 파싱합니다.
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			
			// 액세스 토큰과 리프레시 토큰을 변수에 저장합니다.
			accessToken = element.getAsJsonObject().get("access_token").getAsString();
			refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
			
			br.close();
			bw.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}

 
	/*
액세스 토큰을 이용하여 사용자 정보를 가져오는 메소드
	    GET/POST /v2/user/me
		Host: kapi.kakao.com
		Authorization: Bearer ${ACCESS_TOKEN}/KakaoAK ${APP_ADMIN_KEY}
	 * */
	public HashMap<String, Object> getUserInfo(String accessToken) {
		HashMap<String, Object> userInfo = new HashMap<String, Object>();
		String reqUrl = "https://kapi.kakao.com/v2/user/me";
		try {
			URL url = new URL(reqUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode =" + responseCode);
			
			   // 카카오로부터 받은 응답 데이터를 읽습니다.
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line = "";
			String result = "";
			
			while((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body ="+result);
			
			// 받은 응답 데이터를 Json 객체로 파싱합니다.
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			
			JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
			JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
			
			String nickname = properties.getAsJsonObject().get("nickname").getAsString();
			String email = kakaoAccount.getAsJsonObject().get("email").getAsString();
			
			userInfo.put("nickname", nickname);
			userInfo.put("email", email);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

/*
액세스 토큰을 사용해서 KaKao API에서 로그아웃
POST /v1/user/logout HTTP
Host: kapi.kakao.com
Authorization: Bearer ${ACCESS_TOKEN}/KakaoAK ${APP_ADMIN_KEY}*/
	public void kakaoLogout(String accessToken) {
		String reqURL = "http://kapi.kakao.com/v1/user/logout";
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode = " + responseCode);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String result = "";
			String line = "";
			
			while((line = br.readLine()) != null) {
				result+=line;
			}
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
