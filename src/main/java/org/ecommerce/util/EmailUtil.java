package org.ecommerce.util;

import org.ecommerce.model.Order;
import org.ecommerce.model.OrderItem;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class EmailUtil {

    private static final Logger LOG = Logger.getLogger(EmailUtil.class.getName());
    private static String smtpHost;
    private static int smtpPort;
    private static String mailFrom;
    private static String mailUser;
    private static String mailPassword;

    static {
        try (InputStream in = EmailUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            props.load(in);
            smtpHost     = props.getProperty("mail.smtp.host", "smtp.gmail.com");
            smtpPort     = Integer.parseInt(props.getProperty("mail.smtp.port", "587"));
            mailFrom     = props.getProperty("mail.from", "noreply@fashionhub.com");
            mailUser     = props.getProperty("mail.user", "");
            mailPassword = props.getProperty("mail.password", "");
        } catch (IOException e) {
            LOG.warning("Could not load mail config: " + e.getMessage());
        }
    }

    public static void sendOrderConfirmation(String toEmail, Order order) {
        String subject = "Confirmación de pedido #" + order.getOrderNumber() + " — FashionHub";
        StringBuilder body = new StringBuilder();
        body.append("<h2>¡Gracias por tu compra, ").append(order.getUserName()).append("!</h2>");
        body.append("<p>Tu número de pedido es: <strong>").append(order.getOrderNumber()).append("</strong></p>");
        body.append("<h3>Resumen del pedido:</h3><table border='1' cellpadding='5'>");
        body.append("<tr><th>Producto</th><th>Cantidad</th><th>Precio unitario</th><th>Subtotal</th></tr>");
        for (OrderItem item : order.getItems()) {
            body.append("<tr>")
                .append("<td>").append(item.getProductName()).append("</td>")
                .append("<td>").append(item.getQuantity()).append("</td>")
                .append("<td>$").append(item.getUnitPrice()).append("</td>")
                .append("<td>$").append(item.getSubtotal()).append("</td>")
                .append("</tr>");
        }
        body.append("</table>");
        body.append("<p><strong>Total: $").append(order.getTotal()).append("</strong></p>");
        body.append("<p>Dirección de envío: ").append(order.getShippingAddress()).append("</p>");
        body.append("<p>Método de pago: ").append(order.getPaymentMethod()).append("</p>");
        body.append("<br><p>Equipo FashionHub</p>");

        if (mailUser == null || mailUser.isEmpty()) {
            LOG.info("=== EMAIL (CONSOLE MODE) ===");
            LOG.info("To: " + toEmail);
            LOG.info("Subject: " + subject);
            LOG.info(body.toString().replaceAll("<[^>]+>", ""));
            LOG.info("=== END EMAIL ===");
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailUser, mailPassword);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mailFrom));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject);
            msg.setContent(body.toString(), "text/html; charset=utf-8");
            Transport.send(msg);
            LOG.info("Order confirmation email sent to: " + toEmail);
        } catch (MessagingException e) {
            LOG.warning("Failed to send email to " + toEmail + ": " + e.getMessage());
        }
    }
}
