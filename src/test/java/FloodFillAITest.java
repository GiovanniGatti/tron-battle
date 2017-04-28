import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Flood Fill AI")
class FloodFillAITest implements WithAssertions {

    @Test
    @DisplayName("returns the full map size when no fields are blocked")
    void floodFillEmptyFields() {

        Player.Spot playerSpot = new Player.Spot(0, 0);
        Player.GridSize gridSize = new Player.GridSize(10, 10);
        Player.AvailableSpot availableSpot = (spot) -> !spot.equals(playerSpot);

        int size = Player.FloodFillAI.floodFillArea(availableSpot, gridSize, playerSpot);

        assertThat(size).isEqualTo(99);
    }

    @Test
    @DisplayName("returns 0 when no movement is available")
    void floodFillNoAvailableFields() {
        Player.GridSize gridSize = new Player.GridSize(10, 10);
        Player.AvailableSpot availableSpot = (spot) -> false;

        int size = Player.FloodFillAI.floodFillArea(availableSpot, gridSize, new Player.Spot(0, 0));

        assertThat(size).isEqualTo(0);
    }

    @Test
    @DisplayName("returns correct size when grid is not yet blocked")
    void floodFillNonBlockedMap() {
        // ....
        // .xx.
        // ..x.
        Player.GridSize gridSize = new Player.GridSize(4, 3);
        Player.AvailableSpot availableSpot = (spot) ->
                !spot.equals(new Player.Spot(1, 1))
                        && !spot.equals(new Player.Spot(2, 1))
                        && !spot.equals(new Player.Spot(2, 2));

        int size = Player.FloodFillAI.floodFillArea(availableSpot, gridSize, new Player.Spot(0, 0));

        assertThat(size).isEqualTo(9);
    }

    @Test
    @DisplayName("returns correct size sub-area has been blocked")
    void floodFillBlockedMap() {
        // ....
        // xxx.
        // ..x.
        Player.GridSize gridSize = new Player.GridSize(4, 3);
        Player.AvailableSpot availableSpot = (spot) ->
                !spot.equals(new Player.Spot(0, 1))
                        && !spot.equals(new Player.Spot(1, 1))
                        && !spot.equals(new Player.Spot(2, 1))
                        && !spot.equals(new Player.Spot(2, 2));

        int size = Player.FloodFillAI.floodFillArea(availableSpot, gridSize, new Player.Spot(0, 0));

        assertThat(size).isEqualTo(6);
    }
}