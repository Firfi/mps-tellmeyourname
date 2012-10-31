package ru.megaplan.jira.plugins.tellmeyourname.listener;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.event.user.UserCreatedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.user.UserEvent;
import com.atlassian.jira.event.user.UserEventType;
import com.atlassian.jira.user.UserPropertyManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.event.PluginEventListener;
import com.opensymphony.module.propertyset.PropertySet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import ru.megaplan.jira.plugins.tellmeyourname.service.TellMeYourNameService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/24/12
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class TellMeYourNameListener implements InitializingBean, DisposableBean {

    private static final Logger log = Logger.getLogger(TellMeYourNameListener.class);

    private final EventPublisher eventPublisher;
    private final User botUser;
    private final UserPropertyManager userPropertyManager;
    private final TellMeYourNameService tellMeYourNameService;
    private static final String BOTUSERNAME = "megaplan";
    private static final String META_PREFIX = "jira.meta.";
    private static final String FIRSTNAME_PROPERTY_NAME = "tellmeyourname.firstname";


    TellMeYourNameListener(EventPublisher eventPublisher, UserManager userManager, UserPropertyManager userPropertyManager, TellMeYourNameService tellMeYourNameService) {
        this.userPropertyManager = userPropertyManager;
        this.tellMeYourNameService = tellMeYourNameService;
        botUser = userManager.getUser(BOTUSERNAME);
        this.eventPublisher = eventPublisher;
        try {
            checkNotNull(botUser);
        } catch (NullPointerException e) {
            log.error("bot user : " + BOTUSERNAME + " isn't exist so we can't initialize plugin");
            //throw new IllegalStateException(e);
        }
    }


    @PluginEventListener
    public void pluginUserEvent(UserCreatedEvent userEvent) {
        log.debug("plugin user created event : " + userEvent.getUser());
        handleUserCreation(userEvent.getUser(), null);
    }

    @EventListener
    public void userEvent(UserEvent userEvent)  {
        if (userEvent.getEventType() != UserEventType.USER_CREATED) return;
        User user = userEvent.getUser();
        log.debug("found user created event for user : " + user.getName());
        handleUserCreation(user, userEvent.getInitiatingUser());
    }

    public void handleUserCreation(User user, User creator) {
        if (creator != null && !creator.equals(botUser)) {
            log.debug("initiating user : " + creator.getName() + " isn't equals bot user : " + BOTUSERNAME +
                    " so we do not process event");
        } else {
            log.debug("user created by bot, processing");
            String firstName = tellMeYourNameService.getFirstname(user.getDisplayName());
            if (firstName != null && !firstName.isEmpty()) {
                log.debug("found firstName for user : " + user.getDisplayName() + " firstname : " + firstName);
                PropertySet ps = userPropertyManager.getPropertySet(user);
                ps.setString(META_PREFIX + FIRSTNAME_PROPERTY_NAME, firstName);
            } else {
                log.debug("cannot found firstName for user : " + user.getDisplayName());
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }
}
