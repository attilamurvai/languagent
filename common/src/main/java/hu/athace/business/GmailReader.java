package hu.athace.business;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class GmailReader {
    // todo revise this later
    private static final String APPLICATION_NAME =
            "Gmail API Java Quickstart";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                GmailReader.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        Gmail service = getGmailService();

        // todo check why "me"
        String user = "me";

//        printLabels(service, user);

        List<Message> messages = getMessagesWithLabels(service, user, Collections.singletonList("INBOX"));

        printMessages(service, user, messages);

    }

    private static void printLabels(Gmail service, String user) throws IOException {
        // Print the labels in the user's account.
        ListLabelsResponse listResponse =
                service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.size() == 0) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }
    }

    public static List<Message> getMessagesWithLabels(Gmail service, String userId,
                                                      List<String> labelIds) throws IOException {
        Gmail.Users.Messages.List messageList = service.users().messages().list(userId)
                .setLabelIds(labelIds);
        ListMessagesResponse response = messageList.execute();

        List<Message> messages = new ArrayList<>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = messageList.setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        return messages;
    }

    private static void printMessages(Gmail service, String userId, List<Message> messages) throws IOException {
        for (Message message : messages) {
            // print id and threadId
//            System.out.println(message.toPrettyString());
            Message msg = service.users().messages().get(userId, message.getId()).execute();

            System.out.println(msg.getPayload().getMimeType());

            printHeaderText(msg);

            String content = getContent(msg);
            System.out.println(content);
        }
    }

    private static void printHeaderText(Message message) {
        for (MessagePartHeader header : message.getPayload().getHeaders()) {
            if (Arrays.asList("From", "To", "Date", "Subject").contains(header.getName())) {
                System.out.println(header);
            }
        }
    }

    public static String getContent(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        getPlainTextFromMessagePart(message.getPayload(), stringBuilder);
        return StringUtils.newStringUtf8(Base64.getUrlDecoder().decode(stringBuilder.toString()));
    }


    // todo check if the recursive method is necessary or the relevant plain text is always in the same messagepart
    // todo the method structure/signature might be temporary
    private static void getPlainTextFromMessagePart(MessagePart messagePart, StringBuilder stringBuilder) {
        if (messagePart.getMimeType().equals("text/plain")) {
            stringBuilder.append(messagePart.getBody().getData());
        }

        List<MessagePart> messageParts = messagePart.getParts();
        if (messageParts != null) {
            for (MessagePart subPart : messageParts) {
                getPlainTextFromMessagePart(subPart, stringBuilder);
            }
        }
    }

}