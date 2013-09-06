package org.distributeme.registrywatcher;

import net.anotheria.communication.data.MailFileEntry;
import net.anotheria.communication.data.MultiPartMailMessage;
import net.anotheria.communication.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MailSender class sends eMail messages.
 */
public class MailSender {
	private final MessagingService service = MessagingService.getInstance();

	private String recepientEmail;
	private String senderEmail;
	private String subject;
	
	private static Logger LOG = LoggerFactory.getLogger(RegistryWatcher.class);

	/**
	 * Creates and configures the MailSender class.
	 * 
	 * @param recepientEmail eMail address to send messages to. CAn not be null.
	 * @param senderEmail eMail address identifying the sender.
	 * @param subject subject of the email messages.
	 */
	public MailSender(String recepientEmail, String senderEmail, String subject) {
		if(recepientEmail == null)
			throw new IllegalArgumentException("recepientEmail can not be null");
		if(senderEmail == null)
			throw new IllegalArgumentException("senderEmail can not be null");

		this.recepientEmail = recepientEmail;
		this.senderEmail = senderEmail;
		this.subject = subject;
	}
	
	/**
	 * Sends eMail message with given body and attachments.
	 * 
	 * @param message body of the message. Can not be null.
	 * @param attachments optional array of attachments.
	 * @throws SenderException if fail to sent the message.
	 */
	public void send(String message, MailFileEntry... attachments) throws SenderException {
		MultiPartMailMessage mail = new MultiPartMailMessage();
		
		mail.setSender(senderEmail);
		mail.setRecipient(recepientEmail);
		
		if(subject != null)
			mail.setSubject(subject);
		
		mail.setMessage(message);

		for(MailFileEntry attachment : attachments)
			mail.addFile(attachment);
		
		try {
			service.sendMessage(mail);
			LOG.info("message to " + recepientEmail + " has been sent: " + message);
		} catch(Exception e) {
			throw new SenderException("sending message to " + recepientEmail + " failed", e);
		}
	}
	
	/**
	 * Indicates message sending failure.
	 */
	public static class SenderException extends Exception {
		private static final long serialVersionUID = 5294779781521455491L;

		public SenderException(Throwable cause) {
			super(cause);
		}

		public SenderException(String message) {
			super(message);
		}

		public SenderException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
