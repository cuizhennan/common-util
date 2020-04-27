package util;

import org.joda.time.DateTime;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * util
 *
 * @Author Moxiao 2019-07-10 17:32
 * @Use cron util
 */
public class CronParseUtil {

    public CronParseUtil() {
    }


    /**
     * Parse the given pattern expression.
     */
    public static void parseExpression(String expression) throws IllegalArgumentException {
        String[] fields = StringUtils.tokenizeToStringArray(expression, " ");
        if (!areValidCronFields(fields)) {
            throw new IllegalArgumentException(String.format(
                    "Cron expression must consist of 6 fields (found %d in \"%s\")", fields.length, expression));
        }
    }

    private static boolean areValidCronFields(String[] fields) {
        return (fields != null && fields.length == 6);
    }

    /**
     * 计算下一次任务开始时间
     * cron
     */
    public static Long computeNextTimeForCron(String expression, TriggerContext triggerContext) {
        CronTrigger cronTrigger = new CronTrigger(expression);
        if (null == triggerContext) {
            Date now = DateTime.now().toDate();
            triggerContext = new SimpleTriggerContext(now, now, now);
        }
        return cronTrigger.nextExecutionTime(triggerContext).getTime() / 1000;
    }

    /**
     * 计算下一次任务开始时间
     * fixed rate
     */
    public static Long computeNextTimeForFixedRate(int dot, TimeUnit timeUnit, TriggerContext triggerContext) {
        PeriodicTrigger periodicTrigger = new PeriodicTrigger(dot, timeUnit);
        if (null == triggerContext) {
            Date now = DateTime.now().toDate();
            triggerContext = new SimpleTriggerContext(now, now, now);
        }
        return periodicTrigger.nextExecutionTime(triggerContext).getTime() / 1000;
    }
}
