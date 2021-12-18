import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public record Game(String name, String hyperlink, boolean isNew, boolean isFlash, String rating, String votes, String played) {
    public List<Object> getAsCells() {
        return Arrays.asList(
                "=HYPERLINK(\"" + hyperlink + "\",\"" + name + "\")",
                String.valueOf(this.isNew).toUpperCase(),
                String.valueOf(this.isFlash).toUpperCase(),
                rating,
                votes,
                played.toUpperCase(Locale.ROOT)
        );
    }
}
