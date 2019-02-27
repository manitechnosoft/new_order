package com.mobile.order.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class User implements Serializable {

    @Transient
    static final long serialVersionUID = 1L;

    @Id
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private boolean isEmailVerified;
    private boolean isMobileVerified;
    private String role;
    private String status;
    private boolean forceChangePassword;
    private Date createdAt;
    private String avatar;
    private long credits;
    private long badgeCount;

    @ToMany(referencedJoinProperty = "userId")


    @ToOne(joinProperty = "id")
    private UserPrivilege userPrivilege;


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;



    @Generated(hash = 2099167175)
    public User(long id, String firstName, String lastName, String email, String mobileNumber,
            boolean isEmailVerified, boolean isMobileVerified, String role, String status,
            boolean forceChangePassword, Date createdAt, String avatar, long credits, long badgeCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.isEmailVerified = isEmailVerified;
        this.isMobileVerified = isMobileVerified;
        this.role = role;
        this.status = status;
        this.forceChangePassword = forceChangePassword;
        this.createdAt = createdAt;
        this.avatar = avatar;
        this.credits = credits;
        this.badgeCount = badgeCount;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    @Generated(hash = 1598475601)
    private transient Long userPrivilege__resolvedKey;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Keep
    public String getFullName() {
        return this.firstName + ((this.lastName != null) ? " " + this.lastName : "");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public boolean isMobileVerified() {
        return isMobileVerified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isForceChangePassword() {
        return forceChangePassword;
    }

    public Date getCreatedAt() {
        return this.createdAt == null ? null : new Date(this.createdAt.getTime());
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt == null ? null : new Date(createdAt.getTime());
    }

    public boolean getIsEmailVerified() {
        return this.isEmailVerified;
    }

    public void setIsEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public boolean getIsMobileVerified() {
        return this.isMobileVerified;
    }

    public void setIsMobileVerified(boolean isMobileVerified) {
        this.isMobileVerified = isMobileVerified;
    }

    public boolean getForceChangePassword() {
        return this.forceChangePassword;
    }

    public void setForceChangePassword(boolean forceChangePassword) {
        this.forceChangePassword = forceChangePassword;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCredits() {
        return this.credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }


    public long getBadgeCount() {
        return this.badgeCount;
    }

    public void setBadgeCount(long badgeCount) {
        this.badgeCount = badgeCount;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 76695152)
    public UserPrivilege getUserPrivilege() {
        long __key = this.id;
        if (userPrivilege__resolvedKey == null || !userPrivilege__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserPrivilegeDao targetDao = daoSession.getUserPrivilegeDao();
            UserPrivilege userPrivilegeNew = targetDao.load(__key);
            synchronized (this) {
                userPrivilege = userPrivilegeNew;
                userPrivilege__resolvedKey = __key;
            }
        }
        return userPrivilege;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2054731095)
    public void setUserPrivilege(@NotNull UserPrivilege userPrivilege) {
        if (userPrivilege == null) {
            throw new DaoException(
                    "To-one property 'id' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.userPrivilege = userPrivilege;
            id = userPrivilege.getId();
            userPrivilege__resolvedKey = id;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }


}
