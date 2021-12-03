import java.util.Arrays;
import java.util.List;

public record Game(String name, boolean isFlash) {
    public List<Object> getAsCells() {
        return Arrays.asList(name, String.valueOf(this.isFlash).toUpperCase());
    }
}
