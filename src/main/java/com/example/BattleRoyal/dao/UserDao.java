package com.example.BattleRoyal.dao;

import com.example.BattleRoyal.bean.UserTableEntity;

import com.example.BattleRoyal.responsebean.BaseResponse;
import org.hibernate.exception.ConstraintViolationException;



import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.PersistenceException;
import java.sql.SQLException;

import static com.example.BattleRoyal.responsebean.BaseResponse.ERROR;
import static com.example.BattleRoyal.responsebean.BaseResponse.OK;


public class UserDao extends BaseDao {

    private static UserDao userDao;


    public UserDao() {

    }

    public static UserDao getInstance() {
        if (userDao == null) {
            userDao = new UserDao();
        }
        return userDao;
    }

    public String save(UserTableEntity userTableEntity) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            session.save(userTableEntity);
            transaction.commit();
            baseResponse.setResponseCode(OK);
            baseResponse.setResponseMessage("register success");
        } catch (PersistenceException e) {
            baseResponse.setResponseCode(ERROR);
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException exception = (ConstraintViolationException) e.getCause();
                SQLException sqlException = exception.getSQLException();
                String repeatError = "23000";
                if (sqlException.getSQLState().equals(repeatError)) {
                    baseResponse.setResponseMessage("email is already registered");
                }
                baseResponse.setResponseMessage(sqlException.getMessage());
            } else {
                baseResponse.setResponseMessage(e.getMessage());
            }
        }
        Jsonb json = JsonbBuilder.create();
        return json.toJson(baseResponse);
    }
}
