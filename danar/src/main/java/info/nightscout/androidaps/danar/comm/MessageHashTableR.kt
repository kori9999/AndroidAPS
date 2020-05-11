package info.nightscout.androidaps.danar.comm

import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.dana.DanaPump
import info.nightscout.androidaps.danaRKorean.DanaRKoreanPlugin
import info.nightscout.androidaps.danar.DanaRPlugin
import info.nightscout.androidaps.interfaces.ActivePluginProvider
import info.nightscout.androidaps.interfaces.CommandQueueProvider
import info.nightscout.androidaps.interfaces.ConfigBuilderInterface
import info.nightscout.androidaps.interfaces.DatabaseHelperInterface
import info.nightscout.androidaps.logging.AAPSLogger
import info.nightscout.androidaps.plugins.bus.RxBusWrapper
import info.nightscout.androidaps.plugins.configBuilder.ConstraintChecker
import info.nightscout.androidaps.plugins.general.nsclient.NSUpload
import info.nightscout.androidaps.utils.DateUtil
import info.nightscout.androidaps.utils.resources.ResourceHelper
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageHashTableR @Inject constructor(
    aapsLogger: AAPSLogger,
    rxBus: RxBusWrapper,
    resourceHelper: ResourceHelper,
    constraintChecker: ConstraintChecker,
    danaPump: DanaPump,
    danaRPlugin: DanaRPlugin,
    danaRKoreanPlugin: DanaRKoreanPlugin,
    configBuilderPlugin: ConfigBuilderInterface,
    commandQueue: CommandQueueProvider,
    activePlugin: ActivePluginProvider,
    nsUpload: NSUpload,
    injector: HasAndroidInjector,
    databaseHelper: DatabaseHelperInterface,
    dateUtil: DateUtil
) : MessageHashTableBase {

    var messages: HashMap<Int, MessageBase> = HashMap()

    init {
        put(MsgBolusStop(aapsLogger, rxBus, resourceHelper, danaPump))                 // 0x0101 CMD_MEALINS_STOP
        put(MsgBolusStart(aapsLogger, constraintChecker, danaPump, 0.0))                // 0x0102 CMD_MEALINS_START_DATA
        put(MsgBolusStartWithSpeed(aapsLogger, constraintChecker, danaPump, 0.0, 0))       // 0x0104 CMD_MEALINS_START_DATA_SPEED
        put(MsgBolusProgress(aapsLogger, resourceHelper, rxBus, danaPump))             // 0x0202 CMD_PUMP_THIS_REMAINDER_MEAL_INS
        put(MsgStatusProfile(aapsLogger, danaPump))             // 0x0204 CMD_PUMP_CALCULATION_SETTING
        put(MsgStatusTempBasal(aapsLogger, danaPump, activePlugin, injector))           // 0x0205 CMD_PUMP_EXERCISE_MODE
        put(MsgStatusBolusExtended(injector, aapsLogger, danaPump, activePlugin, dateUtil))       // 0x0207 CMD_PUMP_EXPANS_INS_I
        put(MsgStatusBasic(aapsLogger, danaPump))               // 0x020A CMD_PUMP_INITVIEW_I
        put(MsgStatus(aapsLogger, danaPump))                    // 0x020B CMD_PUMP_STATUS
        // 0x0301 CMD_PUMPINIT_TIME_INFO
        put(MsgInitConnStatusTime(aapsLogger, rxBus, resourceHelper, danaPump, danaRPlugin, danaRKoreanPlugin, configBuilderPlugin, commandQueue, dateUtil))
        put(MsgInitConnStatusBolus(aapsLogger, rxBus, resourceHelper, danaPump))       // 0x0302 CMD_PUMPINIT_BOLUS_INFO
        put(MsgInitConnStatusBasic(aapsLogger, danaPump))       // 0x0303 CMD_PUMPINIT_INIT_INFO
        put(MsgInitConnStatusOption(aapsLogger, rxBus, resourceHelper, danaPump, activePlugin))      // 0x0304 CMD_PUMPINIT_OPTION
        put(MsgSetTempBasalStart(aapsLogger, 0, 0))         // 0x0401 CMD_PUMPSET_EXERCISE_S
        put(MsgSetCarbsEntry(aapsLogger, 0, 0))             // 0x0402 CMD_PUMPSET_HIS_S
        put(MsgSetTempBasalStop(aapsLogger))          // 0x0403 CMD_PUMPSET_EXERCISE_STOP
        put(MsgSetExtendedBolusStop(aapsLogger))      // 0x0406 CMD_PUMPSET_EXPANS_INS_STOP
        put(MsgSetExtendedBolusStart(aapsLogger, constraintChecker, 0.0, 0))     // 0x0407 CMD_PUMPSET_EXPANS_INS_S
        put(MsgError(aapsLogger, rxBus, resourceHelper, danaPump, nsUpload))                     // 0x0601 CMD_PUMPOWAY_SYSTEM_STATUS
        put(MsgPCCommStart(aapsLogger))               // 0x3001 CMD_CONNECT
        put(MsgPCCommStop(aapsLogger))                // 0x3002 CMD_DISCONNECT
        put(MsgHistoryBolus(aapsLogger, rxBus, dateUtil, databaseHelper))              // 0x3101 CMD_HISTORY_MEAL_INS
        put(MsgHistoryDailyInsulin(aapsLogger, rxBus, dateUtil, databaseHelper))       // 0x3102 CMD_HISTORY_DAY_INS
        put(MsgHistoryGlucose(aapsLogger, rxBus, dateUtil, databaseHelper))            // 0x3104 CMD_HISTORY_GLUCOSE
        put(MsgHistoryAlarm(aapsLogger, rxBus, dateUtil, databaseHelper))              // 0x3105 CMD_HISTORY_ALARM
        put(MsgHistoryError(aapsLogger, rxBus, dateUtil, databaseHelper))              // 0x3106 CMD_HISTORY_ERROR
        put(MsgHistoryCarbo(aapsLogger, rxBus, dateUtil, databaseHelper))              // 0x3107 CMD_HISTORY_CARBOHY
        put(MsgHistoryRefill(aapsLogger, rxBus, dateUtil, databaseHelper))             // 0x3108 CMD_HISTORY_REFILL
        put(MsgHistorySuspend(aapsLogger, rxBus, dateUtil, databaseHelper))            // 0x3109 CMD_HISTORY_SUSPEND
        put(MsgHistoryBasalHour(aapsLogger, rxBus, dateUtil, databaseHelper))          // 0x310A CMD_HISTORY_BASAL_HOUR
        put(MsgHistoryDone(aapsLogger, danaPump))               // 0x31F1 CMD_HISTORY_DONT_USED
        // 0x3202 CMD_SETTING_V_BASAL_INS_I
        put(MsgSettingBasal(aapsLogger, danaPump, danaRPlugin))
        // 0x3203 CMD_SETTING_V_MEAL_SETTING_I
        put(MsgSettingMeal(aapsLogger, rxBus, resourceHelper, danaPump, danaRKoreanPlugin))
        put(MsgSettingProfileRatios(aapsLogger, danaPump))      // 0x3204 CMD_SETTING_V_CCC_I
        put(MsgSettingMaxValues(aapsLogger, danaPump))          // 0x3205 CMD_SETTING_V_MAX_VALUE_I
        put(MsgSettingBasalProfileAll(aapsLogger, danaPump))    // 0x3206 CMD_SETTING_V_BASAL_PROFILE_ALL
        put(MsgSettingShippingInfo(aapsLogger, danaPump))       // 0x3207 CMD_SETTING_V_SHIPPING_I
        put(MsgSettingGlucose(aapsLogger, danaPump))            // 0x3209 CMD_SETTING_V_GLUCOSEandEASY
        put(MsgSettingPumpTime(aapsLogger, danaPump, dateUtil))           // 0x320A CMD_SETTING_V_TIME_I
        put(MsgSettingUserOptions(aapsLogger, danaPump))        // 0x320B CMD_SETTING_V_USER_OPTIONS
        put(MsgSettingActiveProfile(aapsLogger, danaPump))      // 0x320C CMD_SETTING_V_PROFILE_NUMBER
        put(MsgSettingProfileRatiosAll(aapsLogger, danaPump))   // 0x320D CMD_SETTING_V_CIR_CF_VALUE
        put(MsgSetSingleBasalProfile(aapsLogger, rxBus, resourceHelper, Array(24) { 0.0 }))     // 0x3302 CMD_SETTING_BASAL_INS_S
        put(MsgSetBasalProfile(aapsLogger, rxBus, resourceHelper, 0.toByte(), Array(24) { 0.0 }))           // 0x3306 CMD_SETTING_BASAL_PROFILE_S
        put(MsgSetUserOptions(aapsLogger, danaPump))            // 0x330B CMD_SETTING_USER_OPTIONS_S
        put(MsgSetActivateBasalProfile(aapsLogger, 0.toByte()))   // 0x330C CMD_SETTING_PROFILE_NUMBER_S
        put(MsgHistoryAllDone(aapsLogger, danaPump))            // 0x41F1 CMD_HISTORY_ALL_DONE
        put(MsgHistoryAll(aapsLogger, rxBus, dateUtil, databaseHelper))                // 0x41F2 CMD_HISTORY_ALL
        put(MsgHistoryNewDone(aapsLogger, danaPump))            // 0x42F1 CMD_HISTORY_NEW_DONE
        put(MsgHistoryNew(aapsLogger, rxBus, dateUtil, databaseHelper))                // 0x42F2 CMD_HISTORY_NEW
        // 0xF0F1 CMD_PUMP_CHECK_VALUE
        put(MsgCheckValue(aapsLogger, danaPump, danaRPlugin))
    }

    override fun put(message: MessageBase) {
        messages[message.command] = message
    }

    override fun findMessage(command: Int): MessageBase {
        return messages[command] ?: MessageBase()
    }
}
