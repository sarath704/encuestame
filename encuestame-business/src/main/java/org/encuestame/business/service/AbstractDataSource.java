/*
 ************************************************************************************
 * Copyright (C) 2001-2009 encuestame: system online surveys Copyright (C) 2009
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.business.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.encuestame.core.exception.EnMeDomainNotFoundException;
import org.encuestame.core.exception.EnMeExpcetion;
import org.encuestame.core.util.ConvertDomainBean;
import org.encuestame.core.util.ConvertListDomainSelectBean;
import org.encuestame.persistence.dao.ICatEmail;
import org.encuestame.persistence.dao.ICatLocation;
import org.encuestame.persistence.dao.ICatLocationTypeDao;
import org.encuestame.persistence.dao.ICatState;
import org.encuestame.persistence.dao.IClientDao;
import org.encuestame.persistence.dao.IFrontEndDao;
import org.encuestame.persistence.dao.IHashTagDao;
import org.encuestame.persistence.dao.INotification;
import org.encuestame.persistence.dao.IPoll;
import org.encuestame.persistence.dao.IProject;
import org.encuestame.persistence.dao.IQuestionDao;
import org.encuestame.persistence.dao.ISecGroups;
import org.encuestame.persistence.dao.ISecPermissionDao;
import org.encuestame.persistence.dao.ISecUserDao;
import org.encuestame.persistence.dao.ISurvey;
import org.encuestame.persistence.dao.ITweetPoll;
import org.encuestame.persistence.dao.imp.CatLocationTypeDao;
import org.encuestame.persistence.dao.imp.ClientDao;
import org.encuestame.persistence.dao.imp.HashTagDao;
import org.encuestame.persistence.dao.imp.NotificationDao;
import org.encuestame.persistence.dao.imp.ProjectDaoImp;
import org.encuestame.persistence.dao.imp.SecUserDaoImp;
import org.encuestame.persistence.domain.CatLocation;
import org.encuestame.persistence.domain.CatState;
import org.encuestame.persistence.domain.HashTag;
import org.encuestame.persistence.domain.Project;
import org.encuestame.persistence.domain.security.SecUserSecondary;
import org.encuestame.utils.web.UnitProjectBean;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;
/**
 * Abstract Data Services.
 * @author Picado, Juan juan@encuestame.org
 * @since April 27, 2009
 * @version $Id: DataSource.java 478 2010-04-07 03:39:10Z dianmorales $
 */
@Service
public abstract class AbstractDataSource{

    /** {@link CatState}. */
    @Resource
    private ICatState stateDao;
    /** {@link CatLocation}. */
    @Resource
    private ICatLocation catLocationDao;
    /** {@link CatLocationTypeDao}. */
    @Resource
    private ICatLocationTypeDao catLocationTypeDao;
    /** {@link ProjectDaoImp}. */
    @Resource
    private IProject projectDaoImp;
    /** {@link ClientDao}. **/
    @Resource
    private IClientDao clientDao;
    /** {@link SecUserDaoImp}. **/
    @Resource
    private ISecUserDao secUserDao;
    /** {@link HashTagDao}. **/
    @Resource
    private IHashTagDao hashTagDao;
    /*** {@link NotificationDao}. **/
    @Resource
    private INotification notificationDao;
    /** {@link FrontEndService}. **/
    @Resource
    private IFrontEndDao frontEndDao;
    /** Log. */
    private Log log = LogFactory.getLog(this.getClass());

    /** {@link IQuestionDao}**/
    @Resource
    private IQuestionDao questionDao;

    /**{@link IPoll}**/
    @Resource
    private IPoll pollDao;

    @Resource
    private ISurvey surveyDaoImp;

    /**{@link ITweetPoll}**/
    @Resource
    private ITweetPoll tweetPollDao;


    /** {@link ISecGroups}. **/
    @Resource
    private ISecGroups groupDao;

    /** {@link ISecPermissionDao} **/
    @Resource
    private ISecPermissionDao permissionDao;

   /** {@link ICatEmail} **/
    @Resource
    private ICatEmail emailListsDao;

    /**
     * Get User.
     * @param username
     * @return user domain
     * @throws EnMeDomainNotFoundException exception
     */
    public SecUserSecondary getUser(final String username) throws EnMeDomainNotFoundException {
        final SecUserSecondary secUserSecondary = getSecUserDao().getUserByUsername(username);
        if(secUserSecondary == null){
            throw new EnMeDomainNotFoundException("user not found");
        } else {
            //TODO: we can add others validations, like is disabled, banned or the account is expired.
            return getSecUserDao().getUserByUsername(username);
        }
    }

    /**
     * Get Primary User Id.
     * @param username
     * @return
     * @throws EnMeDomainNotFoundException exception
     */
    public Long getPrimaryUser(final String username) throws EnMeDomainNotFoundException{
        return getUser(username).getSecUser().getUid();
     }

    /**
     * Load List of Project.
     * @param userId user id.
     * @return {@link Collection} of {@link UnitProjectBean}
     * @throws EnMeExpcetion exception
     */
    public Collection<UnitProjectBean> loadListProjects(final Long userId) {
            final Collection<UnitProjectBean> listProjects = new LinkedList<UnitProjectBean>();
            final Collection<Project> projectList = getProjectDaoImp().findProjectsByUserID(userId);
            log.info("project by user id: "+projectList.size());
            for (Project project : projectList) {
                log.info("adding project "+project.getProjectDescription());
                log.info("groups available in this project "+project.getGroups().size());
                listProjects.add(ConvertDomainBean.convertProjectDomainToBean(project));
            }
            log.info("projects loaded: "+ listProjects.size());
            return listProjects;
    }

    /**
     * Load project info.
     * @param projectBean {@link Project}
     * @return {@link UnitProjectBean}
     * @throws EnMeExpcetion excepcion
     */
    public UnitProjectBean loadProjectInfo(final UnitProjectBean projectBean) throws EnMeExpcetion {
        if (projectBean.getId()!= null) {
            final Project projectDomain = getProjectDaoImp().getProjectbyId(projectBean.getId());
            if (projectDomain != null) {
                final UnitProjectBean projectBeanRetrieved = ConvertDomainBean.convertProjectDomainToBean(projectDomain);
                projectBeanRetrieved.setGroupList(ConvertListDomainSelectBean.convertListGroupDomainToSelect(projectDomain.getGroups()));
                return projectBeanRetrieved;
            } else {
                log.info("id project is not found");
                throw new EnMeExpcetion("id project is not found");
            }
        } else {
            log.info("id project is null");
            throw new EnMeExpcetion("id project is null");
        }
    }

    /**
     * Create Project.
     * @param projectBean {@link UnitProjectBean}
     * @return {@link UnitProjectBean}
     * @throws EnMeExpcetion exception
     */
    public UnitProjectBean createProject(final UnitProjectBean projectBean) throws EnMeExpcetion {
        log.info("create project");
        if (projectBean != null) {
            try {
                final Project projectDomain = new Project();
                //projectDomain.setStateProject(getState(projectBean.getState()));
                projectDomain.setProjectDateFinish(projectBean.getDateFinish());
                projectDomain.setProjectDateStart(projectBean.getDateInit());
                projectDomain.setProjectDescription(projectBean.getName());
                projectDomain.setProjectInfo(projectBean.getDescription());
                projectDomain.setHideProject(projectBean.getHide());
                projectDomain.setNotifyMembers(projectBean.getNotify());
                if(projectBean.getLeader()!=null){
                    projectDomain.setLead(getSecUserDao().getSecondaryUserById(projectBean.getLeader()));
                }
                projectDomain.setUsers(getSecUserDao().getUserById(projectBean.getUserId()));
                getProjectDaoImp().saveOrUpdate(projectDomain);
                projectBean.setId(projectDomain.getProyectId());
                log.debug("created domain project");
            } catch (HibernateException e) {
                throw new EnMeExpcetion(e);
            } catch (Exception e) {
                throw new EnMeExpcetion(e);
            }
            return projectBean;
        } else {
            throw new EnMeExpcetion("project is null");
        }
    }

    /**
     * Create HashTag.
     * @param name tag name
     * @return {@link HashTag}.
     */
    public HashTag createHashTag(final String name){
        final HashTag hashTag = new HashTag();
        hashTag.setHashTag(name);
        getHashTagDao().saveOrUpdate(hashTag);
        return hashTag;
    }

    /**
     * Load Clients on {@link SelectItem}.
     * @param projectId project id
     * @return select items of clients.
     * @throws EnMeExpcetion exception
     */
    public List<SelectItem> loadSelecItemClientsByProjectId(final Long projectId) throws EnMeExpcetion {
        try{
            return ConvertListDomainSelectBean.convertListClientsDomainToSelect(
                   this.getClientDao().findAllClientByProjectId(projectId));
        }
        catch (Exception e) {
            throw new EnMeExpcetion(e);
        }
    }

    /**
     * Load state by id.
     * @param stateId state id
     * @return {@link CatState}
     */
    public CatState getState(final Long stateId) {
        return getStateDao().getState(stateId);
    }

    /**
     * @return the stateDao
     */
    public ICatState getStateDao() {
        return stateDao;
    }

    /**
     * @param stateDao the stateDao to set
     */
    public void setStateDao(final ICatState stateDao) {
        this.stateDao = stateDao;
    }

    /**
     * @return the catLocationDao
     */
    public ICatLocation getCatLocationDao() {
        return catLocationDao;
    }

    /**
     * @param catLocationDao the catLocationDao to set
     */

    public void setCatLocationDao(final ICatLocation catLocationDao) {
        this.catLocationDao = catLocationDao;
    }

    /**
     * @return the projectDaoImp
     */
    public IProject getProjectDaoImp() {
        return projectDaoImp;
    }

    /**
     * @param projectDaoImp the projectDaoImp to set
     */
    public void setProjectDaoImp(final IProject projectDaoImp) {
        this.projectDaoImp = projectDaoImp;
    }

    /**
     * @return the catLocationTypeDao
     */
    public ICatLocationTypeDao getCatLocationTypeDao() {
        return catLocationTypeDao;
    }

    /**
     * @param catLocationTypeDao the catLocationTypeDao to set
     */
    public void setCatLocationTypeDao(final ICatLocationTypeDao catLocationTypeDao) {
        this.catLocationTypeDao = catLocationTypeDao;
    }

    /**
     * @return the clientDao
     */
    public IClientDao getClientDao() {
        return clientDao;
    }

    /**
     * @param clientDao the clientDao to set
     */
    public void setClientDao(final IClientDao clientDao) {
        this.clientDao = clientDao;
    }

    /**
     * @return the secUserDao
     */
    public ISecUserDao getSecUserDao() {
        return secUserDao;
    }

    /**
     * @param secUserDao the secUserDao to set
     */
    public void setSecUserDao(final ISecUserDao secUserDao) {
        this.secUserDao = secUserDao;
    }

    /**
     * @return the questionDao
     */
    public IQuestionDao getQuestionDao() {
        return questionDao;
    }

    /**
     * @param questionDao the questionDao to set
     */
    public void setQuestionDao(IQuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    /**
     * @return the pollDao
     */
    public IPoll getPollDao() {
        return pollDao;
    }

    /**
     * @param pollDao the pollDao to set
     */
    public void setPollDao(IPoll pollDao) {
        this.pollDao = pollDao;
    }

    /**
     * @return the surveyDaoImp
     */
    public ISurvey getSurveyDaoImp() {
        return surveyDaoImp;
    }

    /**
     * @param surveyDaoImp the surveyDaoImp to set
     */
    public void setSurveyDaoImp(ISurvey surveyDaoImp) {
        this.surveyDaoImp = surveyDaoImp;
    }

    /**
     * @return the tweetPollDao
     */
    public ITweetPoll getTweetPollDao() {
        return tweetPollDao;
    }

    /**
     * @param tweetPollDao the tweetPollDao to set
     */
    public void setTweetPollDao(final ITweetPoll tweetPollDao) {
        this.tweetPollDao = tweetPollDao;
    }

    /**
     * @return the groupDao
     */
    public ISecGroups getGroupDao() {
        return groupDao;
    }

    /**
     * @param groupDao the groupDao to set
     */
    public void setGroupDao(ISecGroups groupDao) {
        this.groupDao = groupDao;
    }

    /**
     * @return the permissionDao
     */
    public ISecPermissionDao getPermissionDao() {
        return permissionDao;
    }

    /**
     * @param permissionDao the permissionDao to set
     */
    public void setPermissionDao(ISecPermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    /**
     * @return the emailListsDao
     */
    public ICatEmail getEmailListsDao() {
        return emailListsDao;
    }

    /**
     * @param emailListsDao the emailListsDao to set
     */
    public void setEmailListsDao(ICatEmail emailListsDao) {
        this.emailListsDao = emailListsDao;
    }

    /**
     * @return the hashTagDao
     */
    public IHashTagDao getHashTagDao() {
        return hashTagDao;
    }

    /**
     * @param hashTagDao the hashTagDao to set
     */
    public void setHashTagDao(IHashTagDao hashTagDao) {
        this.hashTagDao = hashTagDao;
    }

    /**
     * @return the notificationDao
     */
    public INotification getNotificationDao() {
        return notificationDao;
    }

    /**
     * @param notificationDao the notificationDao tgetNotificationDaoo set
     */
    public void setNotificationDao(final INotification notificationDao) {
        this.notificationDao = notificationDao;
    }

    /**
     * Getter Front End.
     * @return the frontEndDao
     */
    public IFrontEndDao getFrontEndDao() {
        return frontEndDao;
    }

    /**
     * @param frontEndDao the frontEndDao to set
     */
    public void setFrontEndDao(final IFrontEndDao frontEndDao) {
        this.frontEndDao = frontEndDao;
    }
}