package com.example.BattleRoyal.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


/**
 * @author MirageLee
 */
public class BaseDao {

    SessionFactory sessionFactory;
    Session session;
    Transaction transaction;

    public BaseDao() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }
}
