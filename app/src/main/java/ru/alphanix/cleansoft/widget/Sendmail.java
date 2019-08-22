package ru.alphanix.cleansoft.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ru.alphanix.cleansoft.R;

public class Sendmail {

    //Учетные данные для входа у почтовый ящик. В gmail в безопасности
    //учетной записи должно стоять разрешение "Ненадежные приложения, у которых есть доступ к аккаунту"
    private final String username = "ilya.fanisov";
    private final String password = "HelloWorld";

    private Context act;

    public void sendMail(Context act, StringBuilder messageBody) {
        StringBuilder str = new StringBuilder();
        PackageInfo pInfo = null;
        try {
            pInfo = act.getPackageManager().getPackageInfo(act.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            str.append("version: ").append(pInfo.versionName);
            str.append("\n");
            str.append("versionCode: ").append(pInfo.versionCode);
            str.append("\n");
        }
        str.append(messageBody);
        this.act = act;
        Session session = createSessionObject();

        //Здесь указать почтовый ящик, куда необходимо отправлять письмо
        String email = "***@***.**";
        String subject = "Feedback message";
        try {
            Message message = createMessage(email, subject, str.toString(), session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("sendfrom@gmail.com", "RealOptimizer"));
        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(act, act.getString(R.string.send_mail_wait), act.getString(R.string.send_mail_body), true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                //if (1 == 2)
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
