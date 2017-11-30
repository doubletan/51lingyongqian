package com.example.apple.lingyongqian.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig notesDaoConfig;
    private final DaoConfig companyCardsDaoConfig;
    private final DaoConfig personCardsDaoConfig;
    private final DaoConfig placeCardsDaoConfig;
    private final DaoConfig diarysDaoConfig;
    private final DaoConfig billsDaoConfig;
    private final DaoConfig allInfoDaoConfig;
    private final DaoConfig allCardInfoDaoConfig;
    private final DaoConfig allBillInfoDaoConfig;
    private final DaoConfig autoUserDaoConfig;

    private final UserDao userDao;
    private final NotesDao notesDao;
    private final CompanyCardsDao companyCardsDao;
    private final PersonCardsDao personCardsDao;
    private final PlaceCardsDao placeCardsDao;
    private final DiarysDao diarysDao;
    private final BillsDao billsDao;
    private final AllInfoDao allInfoDao;
    private final AllCardInfoDao allCardInfoDao;
    private final AllBillInfoDao allBillInfoDao;
    private final AutoUserDao autoUserDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        notesDaoConfig = daoConfigMap.get(NotesDao.class).clone();
        notesDaoConfig.initIdentityScope(type);

        companyCardsDaoConfig = daoConfigMap.get(CompanyCardsDao.class).clone();
        companyCardsDaoConfig.initIdentityScope(type);

        personCardsDaoConfig = daoConfigMap.get(PersonCardsDao.class).clone();
        personCardsDaoConfig.initIdentityScope(type);

        placeCardsDaoConfig = daoConfigMap.get(PlaceCardsDao.class).clone();
        placeCardsDaoConfig.initIdentityScope(type);

        diarysDaoConfig = daoConfigMap.get(DiarysDao.class).clone();
        diarysDaoConfig.initIdentityScope(type);

        billsDaoConfig = daoConfigMap.get(BillsDao.class).clone();
        billsDaoConfig.initIdentityScope(type);

        allInfoDaoConfig = daoConfigMap.get(AllInfoDao.class).clone();
        allInfoDaoConfig.initIdentityScope(type);

        allCardInfoDaoConfig = daoConfigMap.get(AllCardInfoDao.class).clone();
        allCardInfoDaoConfig.initIdentityScope(type);

        allBillInfoDaoConfig = daoConfigMap.get(AllBillInfoDao.class).clone();
        allBillInfoDaoConfig.initIdentityScope(type);

        autoUserDaoConfig = daoConfigMap.get(AutoUserDao.class).clone();
        autoUserDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        notesDao = new NotesDao(notesDaoConfig, this);
        companyCardsDao = new CompanyCardsDao(companyCardsDaoConfig, this);
        personCardsDao = new PersonCardsDao(personCardsDaoConfig, this);
        placeCardsDao = new PlaceCardsDao(placeCardsDaoConfig, this);
        diarysDao = new DiarysDao(diarysDaoConfig, this);
        billsDao = new BillsDao(billsDaoConfig, this);
        allInfoDao = new AllInfoDao(allInfoDaoConfig, this);
        allCardInfoDao = new AllCardInfoDao(allCardInfoDaoConfig, this);
        allBillInfoDao = new AllBillInfoDao(allBillInfoDaoConfig, this);
        autoUserDao = new AutoUserDao(autoUserDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(Notes.class, notesDao);
        registerDao(CompanyCards.class, companyCardsDao);
        registerDao(PersonCards.class, personCardsDao);
        registerDao(PlaceCards.class, placeCardsDao);
        registerDao(Diarys.class, diarysDao);
        registerDao(Bills.class, billsDao);
        registerDao(AllInfo.class, allInfoDao);
        registerDao(AllCardInfo.class, allCardInfoDao);
        registerDao(AllBillInfo.class, allBillInfoDao);
        registerDao(AutoUser.class, autoUserDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        notesDaoConfig.getIdentityScope().clear();
        companyCardsDaoConfig.getIdentityScope().clear();
        personCardsDaoConfig.getIdentityScope().clear();
        placeCardsDaoConfig.getIdentityScope().clear();
        diarysDaoConfig.getIdentityScope().clear();
        billsDaoConfig.getIdentityScope().clear();
        allInfoDaoConfig.getIdentityScope().clear();
        allCardInfoDaoConfig.getIdentityScope().clear();
        allBillInfoDaoConfig.getIdentityScope().clear();
        autoUserDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public NotesDao getNotesDao() {
        return notesDao;
    }

    public CompanyCardsDao getCompanyCardsDao() {
        return companyCardsDao;
    }

    public PersonCardsDao getPersonCardsDao() {
        return personCardsDao;
    }

    public PlaceCardsDao getPlaceCardsDao() {
        return placeCardsDao;
    }

    public DiarysDao getDiarysDao() {
        return diarysDao;
    }

    public BillsDao getBillsDao() {
        return billsDao;
    }

    public AllInfoDao getAllInfoDao() {
        return allInfoDao;
    }

    public AllCardInfoDao getAllCardInfoDao() {
        return allCardInfoDao;
    }

    public AllBillInfoDao getAllBillInfoDao() {
        return allBillInfoDao;
    }

    public AutoUserDao getAutoUserDao() {
        return autoUserDao;
    }

}
