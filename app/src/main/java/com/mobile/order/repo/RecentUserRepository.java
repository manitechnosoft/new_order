package com.mobile.order.repo;

import com.mobile.order.BaseApplication;
import com.mobile.order.model.RecentUser;
import com.mobile.order.model.RecentUserDao;

import java.util.List;


public class RecentUserRepository extends BaseRepository {

    private RecentUserDao recentUserDao;

    public RecentUserRepository() {
        load();
    }

    /**
     * Get all logged user list
     *
     * @return LoggedUser
     */
    public List<RecentUser> getLoggedUser(String domain) {
        return recentUserDao
                .queryBuilder()
                .where(RecentUserDao.Properties.Domain.eq(domain))
                .orderDesc(RecentUserDao.Properties.UpdatedAt)
                .list();
    }

    /**
     * Get particular user using domain and id
     *
     * @param domain domain name
     * @param id     User id
     * @return RecentUser object
     */
    public RecentUser findUser(String domain, Long id) {
        return recentUserDao.queryBuilder()
                .where(RecentUserDao.Properties.Domain.eq(domain))
                .where(RecentUserDao.Properties.Id.eq(id))
                .limit(1)
                .unique();
    }

    /**
     * Loads the recentUserDao.
     */
    private void load() {
        recentUserDao = BaseApplication.getDaoInstance().getRecentUserDao();
    }

    /**
     * Method used to delete domain related users
     *
     * @param domain Domain name
     */
    public void deleteUsersOfDomain(String domain) {
        List<RecentUser> list = recentUserDao
                .queryBuilder()
                .where(RecentUserDao.Properties.Domain.eq(domain))
                .list();
        recentUserDao.deleteInTx(list);
    }
}
