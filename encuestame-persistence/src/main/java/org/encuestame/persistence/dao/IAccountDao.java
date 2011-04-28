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
package org.encuestame.persistence.dao;

import java.util.List;

import org.encuestame.persistence.dao.imp.AccountDaoImp;
import org.encuestame.persistence.domain.security.Account;
import org.encuestame.persistence.domain.security.AccountConnection;
import org.encuestame.persistence.domain.security.UserAccount;
import org.encuestame.persistence.domain.security.SocialAccount;
import org.encuestame.persistence.domain.social.SocialProvider;
import org.encuestame.persistence.exception.EnMeNoResultsFoundException;
import org.encuestame.persistence.exception.EnMeExpcetion;
import org.encuestame.utils.oauth.AccessGrant;
import org.encuestame.utils.oauth.OAuth1Token;
import org.hibernate.HibernateException;

/**
 * Interface SecUsers.
 *
 * @author Picado, Juan juan@encuestame.org
 * @since May 05, 2009
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public interface IAccountDao extends IBaseDao {

    /**
     * @param username username
     * @return {@link UserAccount}
     * @throws HibernateException HibernateException
     */
    UserAccount getUserByUsername(final String username);

    /**
     * Retrieve Total Users.
     * @param secUsers
     * @return
     */
    Long retrieveTotalUsers(final Account secUsers);


    /**
     * @return List {@link UserAccount}
     * @throws HibernateException HibernateException
     */
     List<UserAccount> findAll();

     /**
      * Retrieve List of Secondary users without owner account.
      * @param secUsers {@link Account}.
      * @return List of {@link UserAccount}
      */
     List<UserAccount> retrieveListOwnerUsers(final Account secUsers,
                final Integer maxResults, final Integer start);

    /**
     * @param userId userId
     * @return {@link Account}
     * @throws HibernateException HibernateException
     */
    Account getUserById(final Long userId);

    /**
     * @param userId userId
     * @return {@link UserAccount}
     * @throws HibernateException HibernateException
     */
    UserAccount getUserAccountById(final Long userId);

    /**
     * Search user by email
     * @param email email
     * @return
     */
    List<UserAccount> searchUsersByEmail(final String email);

    /**
     * Get one user by email.
     * @param email
     * @return
     */
    UserAccount getUserByEmail(final String email);

    /**
     * Get Twitter Accounts.
     * @param secUsers {@link Account}.
     * @param provider
     * @return List {@link SocialAccount}.
     *
     */
    List<SocialAccount> getSocialAccountByAccount(final Account secUsers,
            final SocialProvider provider);

    /**
     * Get Twitter Account.
     * @param twitterAccountId
     * @return
     */
    SocialAccount getSocialAccountById(final Long socialAccountId);

    /**
     * Get Social Account.
     * @param socialProvider
     * @param socialAccountId
     * @return
     */
    SocialAccount getSocialAccount(final SocialProvider socialProvider, final String socialAccountId);

    /**
     * Get Social Account.
     * @param socialAccountId
     * @param account
     * @return
     */
    SocialAccount getSocialAccount(final Long socialAccountId, final Account account);

    /**
     * Get Twitter Verified Accounts.
     * @param secUsers {@link AccountDaoImp}
     * @param provider {@link SocialProvider}
     * @return List {@link SocialAccount}.
     */
   List<SocialAccount> getTwitterVerifiedAccountByUser(
           final Account secUsers,
           final SocialProvider provider);

   /**
    * Get Total of TweetPoll By User Editor.
    * @param userSecondary
    * @return
    */
   List<Long> getTotalTweetPollByUser(final Long userId);

  /**
   * Get Total of TweetPoll By User Editor.
   * @param userSecondary
   * @return
   */
   List<Long> getTotalPollByUser(final Long userId);


   /**
    * Return {@link UserAccount} by provider name and access token key.
    * @param provider
    * @param accessToken
    * @return
    * @throws EnMeExpcetion
    */
    UserAccount findAccountByConnection(SocialProvider provider,
                      String accessToken) throws EnMeNoResultsFoundException;

    /**
     * Get Provider Account Id.
     * @param accountId
     * @param provider
     * @return
     * @throws EnMeNoResultsFoundException
     */
    Long getProviderAccountId(String accountId, SocialProvider provider)
         throws EnMeNoResultsFoundException;


    /**
     * Get Access Token.
     * @param accountId
     * @param provider
     * @return
     * @throws EnMeNoResultsFoundException
     */
    OAuth1Token getAccessToken(String accountId, SocialProvider provider)
           throws EnMeNoResultsFoundException;

    /**
     * Disconnect Account Connection.
     * @param accountId
     * @param provider
     * @throws EnMeNoResultsFoundException
     */
    void disconnect(String accountId, SocialProvider provider) throws EnMeNoResultsFoundException;

    /**
     * Get Account Connection.
     * @param accountId
     * @param proviver
     * @return
     */
    AccountConnection getAccountConnection(final String accountId, final SocialProvider provider);

    /**
     * {@link AccountConnection} Is Connected.
     * @param accountId
     * @param provider
     * @return
     */
    boolean isConnected(String accountId, SocialProvider provider);

    /**
     *
     * @param provider
     * @param token
     * @param accessGrant
     * @param socialAccountId
     * @param userAccountId
     * @param providerProfileUrl
     * @param socialAccoun
     * @return
     */
    AccountConnection addConnection(
            final SocialProvider provider,
            final OAuth1Token token, //AOAuth1
            final AccessGrant accessGrant, //OAuth2
            final String socialAccountId,
            final Long userAccountId,
            final String providerProfileUrl,
            final SocialAccount socialAccoun);

    /**
     * Retrieve {@link AccountConnection} by access token and provider name.
     * @param provider
     * @param accessToken
     * @return
     * @throws EnMeExpcetion
     */
    AccountConnection findAccountConnectionBySocialProfileId(
                       final SocialProvider provider,
                       final String accessToken);

    /**
     * Get list of id accounts only if are enabled.
     * @return list of id's.
     */
    List<Long> getAccountsEnabled();

    /**
     *
     * @param keyword
     * @param maxResults
     * @param startOn
     * @return
     */
    List<UserAccount> getPublicProfiles(final String keyword,
            final Integer maxResults, final Integer startOn);
}
