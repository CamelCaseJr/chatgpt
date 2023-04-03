import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatbotClient {
    private static final String API_KEY = "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";
    private static final String MODEL_ID = "gpt-3.5-turbo";
    private static final double TEMP_ID = 0.7;


    public static void main(String[] args) {

        StringBuilder texto = new StringBuilder();
        File meuArquivo = new File("src/main/java/arquivo.txt");
        try {
            Scanner scanner = new Scanner(meuArquivo);
            while (scanner.hasNextLine()){
                texto.append(scanner.nextLine());
            }
            scanner.close();
        }catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        HttpClient client = HttpClient.newHttpClient();

        List<ChatMessage> messages = new ArrayList<>();
/*        messages.add(new ChatMessage("system", "You are a helpful assistant."));
        messages.add(new ChatMessage("user", "Who won the world series in 2020?"));
        messages.add(new ChatMessage("assistant", "The Los Angeles Dodgers won the World Series in 2020."));*/
        messages.add(new ChatMessage("user", "analise a redação a seguir " +
                "nos seguintes aspectos:" +
                " estrutura, sequência lógica, parágrafo," +
                " desenvolvimento do texto, " +
                "o conteúdo " +
                "A coesão lexical " +
                "As conjunções e locuções conjuntivas e a " +
                "ortografia." +
                "No final de uma nota de 0 a 10" +
                "Redação:\s" +
                texto));

        String requestBody = "{\"model\": \"" + MODEL_ID
                + "\", \"messages\": " + messagesToJson(messages) + ","
                +"\"temperature\":"+TEMP_ID+"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(createUri())
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static URI createUri() {
        try {
            return new URI("https://api.openai.com/v1/chat/completions");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String messagesToJson(List<ChatMessage> messages) {
        StringBuilder sb = new StringBuilder("[");
        for (ChatMessage message : messages) {
            sb.append("{\"role\": \"").append(message.getRole()).append("\", \"content\": \"").append(message.getContent()).append("\"},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    private static class ChatMessage {
        private String role;
        private String content;


        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
