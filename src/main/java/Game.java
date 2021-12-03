import java.util.Arrays;
import java.util.List;

public record Game(String name, String hyperlink, boolean isFlash, String rating) {
    public List<Object> getAsCells() {
        return Arrays.asList("=HYPERLINK(\"" + hyperlink + "\",\"" + name + "\")", String.valueOf(this.isFlash).toUpperCase(), rating);
    }
}
