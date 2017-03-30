package test.jta.xa;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ItemMessageHandler {

	@JmsListener(destination = "itemqueue")
	public void onMessage(String message) {
		System.out.println("MSG:" + message);
	}

}
