package me.peterrojs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class NBATest {

  private final ByteArrayOutputStream out = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  public void setStreams() {
    System.setOut(new PrintStream(out));
  }

  @AfterEach
  public void restoreInitialStreams() {
    System.setOut(originalOut);
  }

  @Test
  public void testNBAParametersPassing() {
    String[] args = new String[] { "Luka", "Doncic" };
    NBA nba = new NBA();
    new CommandLine(nba).execute(args);

    String[] player = nba.getPlayer();
    assertArrayEquals(args, player);
  }

  @Test
  public void testNBARun() {
    String[] args = new String[] { "Luka", "Doncic" };
    NBA nba = new NBA();
    new CommandLine(nba).execute(args);

    assertEquals(String.join(System.lineSeparator(), new String[] {
      "2020-21 - 10.9",
      "2019-20 - 7.3",
      ""
    }), out.toString());
  }
}
