package io.chocorean.authmod.core;

import io.chocorean.authmod.core.datasource.DataSourcePlayer;
import io.chocorean.authmod.core.datasource.DataSourcePlayerInterface;
import io.chocorean.authmod.core.datasource.DataSourceStrategyInterface;
import io.chocorean.authmod.core.exception.AuthmodError;
import io.chocorean.authmod.core.exception.BannedPlayerError;
import io.chocorean.authmod.core.exception.PlayerAlreadyExistError;
import io.chocorean.authmod.core.exception.PlayerNotFoundError;
import io.chocorean.authmod.core.exception.SamePasswordError;
import io.chocorean.authmod.core.exception.WrongOldPasswordError;
import io.chocorean.authmod.core.validator.DataSourceLoginValidator;
import io.chocorean.authmod.core.validator.DataSourceRegistrationValidator;
import io.chocorean.authmod.core.validator.ValidatorInterface;

public class DataSourceGuard implements GuardInterface {

  private final DataSourceStrategyInterface datasource;
  private final boolean identifierRequired;

  public DataSourceGuard(DataSourceStrategyInterface dataSourceStrategy, boolean identifierRequired) {
    this.datasource = dataSourceStrategy;
    this.identifierRequired = identifierRequired;
  }

  public DataSourceGuard(DataSourceStrategyInterface dataSourceStrategy) {
    this(dataSourceStrategy, false);
  }

  @Override
  public boolean authenticate(PayloadInterface payload) throws AuthmodError {
    ValidatorInterface validator = new DataSourceLoginValidator(this.identifierRequired);
    validator.validate(payload);
    DataSourcePlayerInterface foundPlayer = this.datasource.find(this.getIdentifier(payload));
    if(foundPlayer == null)
      throw new PlayerNotFoundError();
    if(foundPlayer.isBanned()) {
      throw new BannedPlayerError();
    }
    String password = payload.getArgs()[payload.getArgs().length - 1];
    return this.datasource.getHashPassword().check(foundPlayer.getPassword(), password);
  }

  @Override
  public boolean register(PayloadInterface payload) throws AuthmodError {
    ValidatorInterface validator = new DataSourceRegistrationValidator(this.identifierRequired);
    validator.validate(payload);
    DataSourcePlayerInterface playerProxy = new DataSourcePlayer(payload.getPlayer());
    if(identifierRequired)
      playerProxy.setIdentifier(payload.getArgs()[0]);
    if(this.datasource.exist(new DataSourcePlayer(payload.getPlayer())))
      throw new PlayerAlreadyExistError();
    playerProxy.setPassword(this.datasource.getHashPassword().hash(payload.getArgs()[payload.getArgs().length - 1]));
    return this.datasource.add(playerProxy);
  }

  private String getIdentifier(PayloadInterface payload) {
    return this.identifierRequired ? payload.getArgs()[0] : payload.getPlayer().getUsername();
  }

  @Override
  public boolean update(PayloadInterface oldPayload, PayloadInterface newPayload) throws AuthmodError {
    // Checking old password
    ValidatorInterface validator = new DataSourceLoginValidator(false);
    validator.validate(oldPayload);
    DataSourcePlayerInterface foundPlayer = this.datasource.find(this.getIdentifier(oldPayload));
    String oldPassword = oldPayload.getArgs()[oldPayload.getArgs().length - 1];
    if (!this.datasource.getHashPassword().check(foundPlayer.getPassword(), oldPassword))
      throw new WrongOldPasswordError();
    // Checking new password
    validator = new DataSourceRegistrationValidator(false);
    validator.validate(newPayload);
    String newPassword = newPayload.getArgs()[newPayload.getArgs().length - 1];
    if (oldPassword.contentEquals(newPassword))
      throw new SamePasswordError();
    // Updating password
    DataSourcePlayerInterface playerProxy = new DataSourcePlayer(newPayload.getPlayer());
    playerProxy.setPassword(this.datasource.getHashPassword().hash(newPassword));
    // OU ICI
    return this.datasource.update(playerProxy);
  }
}
