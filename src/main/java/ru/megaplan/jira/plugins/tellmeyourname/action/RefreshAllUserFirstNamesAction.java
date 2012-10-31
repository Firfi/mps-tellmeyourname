package ru.megaplan.jira.plugins.tellmeyourname.action;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugins.tellmeyourname.listener.TellMeYourNameListener;
import ru.megaplan.jira.plugins.tellmeyourname.service.TellMeYourNameService;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/24/12
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefreshAllUserFirstNamesAction extends JiraWebActionSupport {

    private final static Logger log = Logger.getLogger(RefreshAllUserFirstNamesAction.class);

    private final TellMeYourNameService tellMeYourNameService;
    private final GroupManager groupManager;
    private final UserManager userManager;
    private final TellMeYourNameListener tellMeYourNameListener;

    RefreshAllUserFirstNamesAction(TellMeYourNameService tellMeYourNameService, GroupManager groupManager, UserManager userManager, TellMeYourNameListener tellMeYourNameListener) {
        this.groupManager =groupManager;
        this.userManager = userManager;
        this.tellMeYourNameService = tellMeYourNameService;
        this.tellMeYourNameListener = tellMeYourNameListener;
    }

    public String doExecute() {
        User creator = userManager.getUser("megaplan");
        for (User u : groupManager.getUsersInGroup(groupManager.getGroup("email-accounts"))) {
            tellMeYourNameListener.handleUserCreation(u, creator);
        }
        return SUCCESS;
    }
}
