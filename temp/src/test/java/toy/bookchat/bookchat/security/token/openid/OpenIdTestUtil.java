package toy.bookchat.bookchat.security.token.openid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class OpenIdTestUtil {

    private BufferedReader privateKeyReader;
    private BufferedReader publicKeyReader;

    public OpenIdTestUtil(String privateKey, String publicKey) throws FileNotFoundException {
        this.privateKeyReader = new BufferedReader(new FileReader(privateKey));
        this.publicKeyReader = new BufferedReader(new FileReader(publicKey));
    }

    public String getPrivateKey(int lineSize) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        this.privateKeyReader.readLine();
        for (int i = 0; i < lineSize - 2; i++) {
            stringBuilder.append(this.privateKeyReader.readLine());
        }
        return stringBuilder.toString();
    }

    public String getPublicKey(int lineSize) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        this.publicKeyReader.readLine();
        for (int i = 0; i < lineSize - 2; i++) {
            stringBuilder.append(this.publicKeyReader.readLine());
        }

        return stringBuilder.toString();
    }
}
