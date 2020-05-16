package io.chocorean.authmod.command;

import io.chocorean.authmod.core.DataSourceGuard;
import io.chocorean.authmod.core.Payload;
import io.chocorean.authmod.core.PayloadInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ChangePasswordCommandTest extends CommandTest {

  @BeforeEach
  public void init() throws Exception {
    super.initProperties();
    this.registerPlayer();
  }

  @Test
  void testExecute() {
    this.handler.authorizePlayer(this.playerEntity);
    int res = ChangePasswordCommand.execute(
      this.source,
      this.handler,
      this.guard,
      this.createPayload("baguette")
    );
    assertEquals(0, res);
  }

  @Test
  void testExecuteIdentifierRequired() throws Exception {
    File file = Paths.get(System.getProperty("java.io.tmpdir"), "authmod.csv").toFile();
    Files.deleteIfExists(file.toPath());
    this.guard = new DataSourceGuard(this.dataSource, true);
    this.guard.register(new Payload(this.player, new String[]{"Bernard", this.password, this.password}));
    this.handler.authorizePlayer(this.playerEntity);
    int res = ChangePasswordCommand.execute(
      this.source,
      this.handler,
      this.guard,
      this.createPayload("baguette")
    );
    assertEquals(0, res);
  }

  @Test
  void testWrongOldPassword() {
    this.handler.authorizePlayer(this.playerEntity);
    int res = ChangePasswordCommand.execute(this.source, this.handler, this.guard, this.createPayload(
      "pain au chocolat",
      "chausson au pommes",
      "chausson au pommes"
      ));
    assertNotEquals(0, res);
  }
  
  @Test
  void testSamePassword() {
    int res = ChangePasswordCommand.execute(this.source, this.handler, this.guard, this.createPayload(this.password));
    assertNotEquals(0, res);
  }
  
  @Test
  void testNotLogged() {
    int res = ChangePasswordCommand.execute(this.source, this.handler, this.guard, this.createPayload("opera"));
    assertNotEquals(0, res);
    assertFalse(this.handler.isLogged(this.playerEntity));
  }

  private PayloadInterface createPayload(String oldPassword, String newPassword, String confirmation) {
    return new Payload(this.player, new String[] {oldPassword, newPassword, confirmation});
  }

  private PayloadInterface createPayload(String newPassword) {
    return new Payload(this.player, new String[] {this.password, newPassword, newPassword});
  }

}
