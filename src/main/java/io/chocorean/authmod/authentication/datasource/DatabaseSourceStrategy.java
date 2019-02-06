package io.chocorean.authmod.authentication.datasource;

import io.chocorean.authmod.AuthMod;
import io.chocorean.authmod.authentication.db.ConnectionFactory;
import io.chocorean.authmod.authentication.db.IConnectionFactory;
import io.chocorean.authmod.authentication.db.IPlayersDAO;
import io.chocorean.authmod.authentication.db.PlayersDAO;
import io.chocorean.authmod.config.AuthModDatabaseConfig;
import io.chocorean.authmod.exception.AuthmodException;
import io.chocorean.authmod.exception.LoginException;
import io.chocorean.authmod.exception.PlayerAlreadyExistException;
import io.chocorean.authmod.exception.RegistrationException;
import io.chocorean.authmod.model.IPlayer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseSourceStrategy implements IDataSourceStrategy {

    private final IPlayersDAO<IPlayer> playersDAO;
    private static final Logger LOGGER = AuthMod.LOGGER;

    public DatabaseSourceStrategy(IConnectionFactory connectionFactory) throws SQLException {
        this.playersDAO = new PlayersDAO("players", connectionFactory);
    }

    @Override
    public IPlayer find(String email, String username) {
        /*IPlayer p;
        LOGGER.info("Checking authentication for " + player.getEmail());
        try {
            p = this.playersDAO.findFirst(player);
        } catch(SQLException e) {
            LOGGER.catching(Level.ERROR, e);
            throw new LoginException(AuthMod.getConfig().getDatabaseErrorMsg());
        }
        return p;
        */
        return null;
    }

    @Override
    public boolean add(IPlayer player) throws RegistrationException {
        /*try {
            IPlayer saved = this.playersDAO.findFirst(player);
            if(saved != null) {
                throw new PlayerAlreadyExistException(AuthMod.getConfig().getPlayerAlreadyExistsMsg());
            }
            this.playersDAO.create(player);
        } catch(SQLException e) {
            LOGGER.catching(Level.ERROR, e);
            throw new LoginException(AuthMod.getConfig().getDatabaseErrorMsg());
        }*/
        return false;
    }

    @Override
    public boolean exist(IPlayer player) {
        try {
            return this.playersDAO.findFirst(player) != null;
        } catch (SQLException e) {
            LOGGER.catching(Level.ERROR, e);
        }
        return false;
    }

}
