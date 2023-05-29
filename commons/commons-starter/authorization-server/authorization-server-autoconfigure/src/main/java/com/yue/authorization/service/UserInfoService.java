package com.yue.authorization.service;

import org.springframework.jdbc.core.JdbcOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 定制token信息
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public UserInfoService(JdbcOperations jdbcOperations){
        this.userInfoRepository=new UserInfoRepository(jdbcOperations);
    }

    public UserInfo loadUser(String username) {
        return new UserInfo(this.userInfoRepository.findByUsername(username));
    }

    static class UserInfoRepository {

        private static final String COLUMN_NAMES = "id,username,nickname";
        private static final String TABLE_NAME = "user";
        private static final String LOAD_REGISTERED_CLIENT_SQL = "SELECT " + COLUMN_NAMES + " FROM " + TABLE_NAME + " WHERE ";
        private static final String PK_FILTER = "username = ?";
        private final JdbcOperations jdbcOperations;
        private final Map<String, Map<String, Object>> userInfo = new HashMap<>();

        public UserInfoRepository(JdbcOperations jdbcOperations){
            this.jdbcOperations=jdbcOperations;
        }

        public Map<String, Object> findByUsername(String username) {
            Map<String, Object> map = this.userInfo.get(username);
            if(map == null){
                Map<String, Object> user = createUser(username);
                userInfo.put(username,user);
                return user;
            }
            return map;
        }

        private Map<String, Object> createUser(String username) {
            UserInfo user = findByName(username);
            if(user == null ){
                return null;
            }
            return user.getClaims();
        }

        private UserInfo findByName(String name){
            List<UserInfo> users=this.jdbcOperations.query(LOAD_REGISTERED_CLIENT_SQL + PK_FILTER,(rs, rowNum) -> {
                return UserInfo.builder()
                        .userId(rs.getInt("id"))
                        .username(rs.getString("username"))
                        .nickname(rs.getString("nickname"))
                        .build();
            }, name);
            return users.isEmpty()?null: users.get(0);
        }
    }
}
