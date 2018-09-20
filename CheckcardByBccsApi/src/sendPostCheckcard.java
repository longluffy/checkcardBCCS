import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import cardprocess.hibernate.CardProcess;

public class sendPostCheckcard {

	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";

	public static void main(String[] args) throws Exception {

//		sendPostCheckcard http = new sendPostCheckcard();
//
//		System.out.println("\nTesting 2 - Send Http POST request");
//		http.sendPost(args);

	}

	// HTTP POST request
	public void sendPost(String jseesionid1, String jseesionid2, CardProcess cardInput) throws Exception {
//		jseesionid1 = "314365BF99AFB138F36332A28D97496C.BCCS_CC2_51_8686";
//		jseesionid2 = "4108CA9DFC537F3E161D89569F45196F.BCCS_CC2_51_8686";

		String cardserialnumber = cardInput.getSerial();
		// "10001012543685";
		String url = "http://10.240.147.246/BCCS_CC/stockCard.jsf";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Accept", "application/xml, text/xml, */*; q=0.01");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		con.setRequestProperty("Faces-Request", "partial/ajax");
		con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		con.setRequestProperty("Referer", "http://10.240.147.246/BCCS_CC/stockCard.jsf");
		con.setRequestProperty("Content-Length", "502");
		con.setRequestProperty("Cookie", "JSESSIONID=" + jseesionid1 + "; JSESSIONID=" + jseesionid2
				+ "; sentinel_menumode=slim; VtToolkit=%7B%22display%22%3A%22float%22%2C%22position%22%3A%7B%22top%22%3A%22auto%22%2C%22left%22%3A%22auto%22%2C%22bottom%22%3A%2230px%22%2C%22right%22%3A%2230px%22%7D%7D; sentinel_activemenuitem=sidebarLeft%3Asm_leftmenu_0");
		con.setRequestProperty("KPI_VTT", "937c4131e19ac3d7b4ab27960c5d1e42");
		con.setRequestProperty("X-Hello", "World");
		con.setRequestProperty("VTS-KPIID", "45820861784");
		con.setRequestProperty("VTS-IP", "L7kRpL7NaPeBVR9nyXo6lfFltr+/bDocR764OMBPpNo=");
		con.setRequestProperty("VTS-MAC", "4s/EoKFu/L25nFqxMKfgkahsO/rq/cZkT8GzKgZg36Y=");
		con.setRequestProperty("VTS-VER", "6upVXJWPWngIGP3uIwWZiEraU8BIaVIXDT48cJFUuto=");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("Pragma", "no-cache");
		con.setRequestProperty("Cache-Control", "no-cache");
		con.setRequestProperty("X-Hello", "World");

		String urlParameters = "javax.faces.partial.ajax=true&javax.faces.source=j_idt83:j_idt96&javax.faces.partial.execute=j_idt83:searchFormUtilitiesCardNumberInfor&javax.faces.partial.render=j_idt83:updateSearchCardNumber+j_idt83:msgSearchCardNumber&j_idt83:j_idt96=j_idt83:j_idt96&kpi=cardNumberController.searchCardNumber()&j_idt83:chooseFormsRecharge_focus=&j_idt83:chooseFormsRecharge_input=1&j_idt83:inputNumberSerialCardNumber="
				+ cardserialnumber + "&javax.faces.ViewState=-5602288681543480963:5458447100201618628";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(con.getInputStream())));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			if (!inputLine.isEmpty()) {
				// set value to card input
				if (inputLine.contains("Không có thông tin tổng đài")) {
					// the cao khong hop le
					cardInput.setCardavailable(0);
					cardInput.setCardcheckresult("Không có thông tin tổng đài");
					break;
				}
				lookforState(inputLine, cardInput);
				lookforValue(inputLine, cardInput);
			}
		}
		in.close();

	}

	private void lookforValue(String inputLine, CardProcess cardInput) {
		// TODO Auto-generated method stub
		if (inputLine.contains("j_idt83:j_idt199")) {
			if (inputLine.contains("10.000")) {
				cardInput.setCardvalue(BigInteger.valueOf(10000l));
			} else if (inputLine.contains("20.000")) {
				cardInput.setCardvalue(BigInteger.valueOf(20000l));
			} else if (inputLine.contains("50.000")) {
				cardInput.setCardvalue(BigInteger.valueOf(50000l));
			} else if (inputLine.contains("100.000")) {
				cardInput.setCardvalue(BigInteger.valueOf(100000l));
			} else if (inputLine.contains("200.000")) {
				cardInput.setCardvalue(BigInteger.valueOf(200000l));
			} else if (inputLine.contains("500.000")) {
				cardInput.setCardvalue(BigInteger.valueOf(500000l));
			}
		}
	}

	private void lookforState(String inputLine, CardProcess cardInput) {
		// TODO Auto-generated method stub
		if (inputLine.contains("j_idt83:j_idt190")) {
			if (inputLine.contains("Thẻ chưa sử dụng")) {
				cardInput.setCardavailable(1);
				cardInput.setCardcheckresult("Thẻ chưa sử dụng");
			} else if (inputLine.contains("Thẻ đã sử dụng")) {
				cardInput.setCardavailable(0);
				cardInput.setCardcheckresult("Thẻ đã sử dụng");
			} else {
				cardInput.setCardavailable(0);
			}
		}
	}

}
