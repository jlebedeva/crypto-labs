
package main;

/**
 * @author Yana Lebedeva <jlebedeva@jet.msk.su>
 */
public class Task {

    public enum Mode { CBC, CTR };
    public enum Padding { PKCS5 };
    private Mode mode;
    private Padding padding;

    private String key = "<unknown>";
    private String cyphertext = "<unknown>";
    private String message = "<unknown>";

    public Task(Mode mode, Padding padding, String key, String cyphertext) {
        this.mode = mode;
        this.padding = padding;
        this.key = key;
        this.cyphertext = cyphertext;
    }

    public Padding getPadding() {
        return padding;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCyphertext() {
        return cyphertext;
    }

    public void setCyphertext(String cypher) {
        this.cyphertext = cypher;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(printProperty("Mode", getMode().toString()));
        sb.append(printProperty("Key", getKey()));
        sb.append(printProperty("Cyphertext", getCyphertext()));
        sb.append(printProperty("Message", getMessage()));
        return sb.toString();
    }

    private String printProperty(String name, String value) {
        return name + ": " + value + "\n";
    }
}
