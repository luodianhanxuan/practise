package com.wangjg.ldap;

import com.novell.ldap.*;
import com.novell.ldap.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
public class LdapDemo {

    /*
        <!--ldap-->
        <dependency>
            <groupId>com.novell.ldap</groupId>
            <artifactId>jldap</artifactId>
            <version>4.3</version>
            <type>jar</type>
            <scope>compile</scope>
        </dependency>
     */


    private static final String LDAP_HOST = "localhost";
    private static final int LDAP_PORT = LDAPConnection.DEFAULT_PORT;
    private static final int LDAP_VERSION = LDAPConnection.LDAP_V3;
    private static final String BASE_DN = "cn=admin,dc=xyz,dc=com";
    private static final String ADMIN_DN = "cn=admin,dc=xyz,dc=com";
    private static final String ADMIN_PASSWORD = "123456";

    @Test
    public void search() throws LDAPException {
        @SuppressWarnings("UnnecessaryLocalVariable")
        String loginDN = ADMIN_DN;

        String searchBase = "dc=xyz,dc=com";
        String searchFilter = "objectClass=*";

        int searchScope = LDAPConnection.SCOPE_SUB;
        LDAPConnection lc = new LDAPConnection();
        lc.connect(LDAP_HOST, LDAP_PORT);
        lc.bind(LDAP_VERSION, loginDN, ADMIN_PASSWORD.getBytes(StandardCharsets.UTF_8));
        LDAPSearchResults searchResults = lc.search(searchBase,
                searchScope, searchFilter, null, false);

        while (searchResults.hasMore()) {
            LDAPEntry entry;
            try {
                entry = searchResults.next();
            } catch (LDAPException e) {
                log.error("Error: " + e);
                if (e.getResultCode() == LDAPException.LDAP_TIMEOUT
                        || e.getResultCode() == LDAPException.CONNECT_ERROR) {
                    break;
                } else {
                    continue;
                }
            }

            log.info("DN =: " + entry.getDN());
            log.info("|---- Attributes list: ");

            LDAPAttributeSet attributeSet = entry.getAttributeSet();
            for (Object item : attributeSet) {
                if (!(item instanceof LDAPAttribute)) {
                    continue;
                }
                LDAPAttribute attr = (LDAPAttribute) item;
                String name = attr.getName();
                String value = attr.getStringValue();
                if (!Base64.isLDIFSafe(value)) {
                    // base64 encode and then print out
                    value = Base64.encode(value.getBytes());
                }
                log.info("|---- ---- " + name
                        + " = " + value);
            }
        }
    }


    @Test
    public void add() {
        String containerName = "dc=xyz,dc=com";

        LDAPConnection lc = new LDAPConnection();
        LDAPAttributeSet attributeSet = new LDAPAttributeSet();
        LDAPEntry newEntry;

        // 创建部门
        attributeSet.add(new LDAPAttribute("objectClass", "organizationalUnit"));
        String dn = "ou=Developer," + containerName;


        // 创建员工
//        attributeSet.add(new LDAPAttribute("objectClass", "inetOrgPerson"));
//        attributeSet.add(new LDAPAttribute("cn", "Wukong Sun"));
//        attributeSet.add(new LDAPAttribute("sn", "Sun"));
//        attributeSet.add(new LDAPAttribute("mail", "Wukong.Sun@gmail.com"));
//        attributeSet.add(new LDAPAttribute("labeledURI",
//                "http://www.baidu.com"));
//        attributeSet.add(new LDAPAttribute("userPassword", "123123123"));
//        String dn = "uid=addnew,ou=Developer," + containerName;

        newEntry = new LDAPEntry(dn, attributeSet);
        try {
            lc.connect(LDAP_HOST, LDAP_PORT);
            lc.bind(LDAP_VERSION, ADMIN_DN, ADMIN_PASSWORD.getBytes(StandardCharsets.UTF_8));
            log.info("login ldap server successfully.");
            lc.add(newEntry);
            log.info("Added object: " + dn + " successfully.");
        } catch (LDAPException e) {
            e.printStackTrace();
        } finally {
            try {
                if (lc.isConnected()) {
                    lc.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void del() {
        String containerName = "dc=xyz,dc=com";
        LDAPConnection lc = new LDAPConnection();
        try {
            // 删除 部门
            String dn = "ou=Developer," + containerName;
            // 删除员工
//            String dn = "uid=addnew,ou=Developer," + containerName;

            lc.connect(LDAP_HOST, LDAP_PORT);
            lc.bind(LDAP_VERSION, ADMIN_DN, ADMIN_PASSWORD.getBytes(StandardCharsets.UTF_8));
            lc.delete(dn);
            log.info(" delete Entry: " + dn + " success.");
            lc.disconnect();
        } catch (LDAPException e) {
            e.printStackTrace();
        } finally {
            try {
                if (lc.isConnected()) {
                    lc.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void modify() {
        String modifyDN = "uid=addnew,ou=Developer,dc=xyz,dc=com";

        LDAPConnection lc = new LDAPConnection();

        List<LDAPModification> modList = new ArrayList<>();

        // add a new value to the description attribute
        String desc = "This object was modified at " + new Date();
        LDAPAttribute attribute = new LDAPAttribute("description", desc);
        modList.add(new LDAPModification(LDAPModification.ADD, attribute));

        attribute = new LDAPAttribute("telephoneNumber", "133-2222-xxxx");
        modList.add(new LDAPModification(LDAPModification.ADD, attribute));

        // replace the labeledURI address with a new value
        attribute = new LDAPAttribute("labeledURI", "baidu.com");
        modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));

        // delete the email attribute
        attribute = new LDAPAttribute("mail");
        modList.add(new LDAPModification(LDAPModification.DELETE, attribute));

        LDAPModification[] mods = new LDAPModification[modList.size()];
        mods = modList.toArray(mods);

        try {
            lc.connect(LDAP_HOST, LDAP_PORT);
            lc.bind(LDAP_VERSION, ADMIN_DN, ADMIN_PASSWORD.getBytes(StandardCharsets.UTF_8));
            lc.modify(modifyDN, mods);
            log.info("LDAPAttribute add、replace、delete all successful.");
        } catch (LDAPException e) {
            e.printStackTrace();
        } finally {
            try {
                if (lc.isConnected()) {
                    lc.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void verifyPassword() {
        String verifyDN = "uid=addnew,ou=Developer,dc=xyz,dc=com";
        String verifyPassword = "123123123";
        LDAPConnection lc = new LDAPConnection();

        try {
            lc.connect(LDAP_HOST, LDAP_PORT);
            lc.bind(LDAP_VERSION, ADMIN_DN, ADMIN_PASSWORD.getBytes(StandardCharsets.UTF_8));
            LDAPAttribute attr = new LDAPAttribute("userPassword",
                    verifyPassword);

            boolean correct = lc.compare(verifyDN, attr);
            log.info(correct ? "The password is correct.^_^"
                    : "The password is incorrect.!!!");
        } catch (LDAPException e) {
            e.printStackTrace();
            if (e.getResultCode() == LDAPException.NO_SUCH_OBJECT) {
                log.info("Error: No such entry");
            } else if (e.getResultCode() == LDAPException.NO_SUCH_ATTRIBUTE) {
                log.info("Error: No such attribute");
            } else {
                log.info("Error: " + e);
            }
        } finally {
            try {
                if (lc.isConnected()) {
                    lc.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
