package com.tierep.twitterlists.twitter4jcache;

import android.util.Log;

import java.util.HashMap;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * This class is a wrapper around twitter4j.Twitter class that provides a simple cache for the most
 * common GET-requests made throughout this application.
 *
 * Created by pieter on 04/02/15.
 */
public class TwitterCache {

    private Twitter twitter;

    private HashMap<Long, HashMap<Long, PagableResponseList<User>>> cacheUserListMembers;
    private HashMap<Long, HashMap<Long, PagableResponseList<User>>> cacheFriendsListMember;

    public TwitterCache (Twitter twitter) {
        this.twitter = twitter;
        cacheUserListMembers = new HashMap<>();
        cacheFriendsListMember = new HashMap<>();
    }

    public PagableResponseList<User> getUserListMembers(final long listId, final long cursor) throws TwitterException {
        return lookupInCache(cacheUserListMembers, listId, cursor, new Fetcher() {
            @Override
            public PagableResponseList<User> fetch() throws TwitterException {
                return twitter.getUserListMembers(listId, cursor);
            }
        });
    }

    public PagableResponseList<User> getFriendsList(final long userId, final long cursor) throws TwitterException {
        return lookupInCache(cacheFriendsListMember, userId, cursor, new Fetcher() {
            @Override
            public PagableResponseList<User> fetch() throws TwitterException {
                return twitter.getFriendsList(userId, cursor, 200);
            }
        });
    }

    private PagableResponseList<User> lookupInCache(HashMap<Long, HashMap<Long, PagableResponseList<User>>> cache, long key1, long key2, Fetcher fetcher) throws TwitterException{
        HashMap<Long, PagableResponseList<User>> hashMapCursor = cache.get(key1);
        if (hashMapCursor != null) {
            PagableResponseList<User> cachedValue = hashMapCursor.get(key2);
            if (cachedValue != null) {
                Log.d("CACHE", "Cache hit");
                return cachedValue;
            } else {
                PagableResponseList<User> result = fetcher.fetch();
                Log.d("CACHE", "Cache miss");
                hashMapCursor.put(key2, result);
                return result;
            }
        } else {
            PagableResponseList<User> result = fetcher.fetch();
            Log.d("CACHE", "Cache miss");
            HashMap<Long, PagableResponseList<User>> cacheForSpecificList = new HashMap<>();
            cacheForSpecificList.put(key2, result);
            cache.put(key1, cacheForSpecificList);
            return result;
        }
    }

    private interface Fetcher {
        public PagableResponseList<User> fetch() throws TwitterException;
    }

    /**
     * Empty the caches.
     */
    public void invalidateAll() {
        cacheUserListMembers = new HashMap<>();
        cacheFriendsListMember = new HashMap<>();
    }

    // TODO dit zou geinvalideerd kunnen worden per ID
    public void invalidateUserListMembers() {
        cacheUserListMembers = new HashMap<>();
        Log.d("CACHE", "Clear cache UserListMembers");
    }

    public void invalidateFriendListMembers() {
        cacheFriendsListMember = new HashMap<>();
    }
}
