import cardprocess.hibernate.CardProcess;

public class tesstSendpost {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String jseesionid1 = "CAA7BF08F2F84EC506DD42DC727B04ED.BCCS_CC2_51_8686";
		String jseesionid2 = "969ADA9308F3E3FF08753082E80D5039.BCCS_CC2_51_8686";
		
		CardProcess cardInput = new CardProcess();
		cardInput.setSerial("10201012543684");
		
		sendPostCheckcard cardservice = new sendPostCheckcard();
		try {
			cardservice.sendPost(jseesionid1, jseesionid2, cardInput);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(cardInput);
		
	}

}
