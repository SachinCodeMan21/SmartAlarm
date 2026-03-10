package com.example.smartalarm.feature.alarm.presentation.view.fragment.editor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.permission.MyAppPermissionRequester
import com.example.smartalarm.core.framework.permission.MyPermissionChecker
import com.example.smartalarm.core.framework.permission.PermissionFlowDelegate
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.core.framework.permission.model.AppFeature
import com.example.smartalarm.core.framework.permission.model.MyAppPermission
import com.example.smartalarm.core.framework.permission.model.RequesterType
import com.example.smartalarm.core.framework.permission.model.Requirement
import com.example.smartalarm.databinding.FragmentAlarmEditorBinding
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorSystemEvent
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.binder.AlarmEditorUiBinder
import com.example.smartalarm.feature.alarm.presentation.view.handler.AlarmEditorEffectHandler
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import com.example.smartalarm.feature.alarm.utility.getParcelableExtraCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment responsible for creating and editing alarms.
 *
 * This fragment uses a **delegation pattern** to separate concerns:
 * 1. [AlarmEditorUiBinder] – Handles UI rendering, view binding, and user interactions.
 * 2. [AlarmEditorEffectHandler] – Handles transient side-effects such as navigation, toasts, and launching system pickers.
 * 3. [PermissionFlowDelegate] – Manages runtime and special permission flows (notifications, exact alarms, full-screen intents).
 *
 * [AlarmEditorViewModel] is the single source of truth for UI state and events.
 *
 * Key responsibilities:
 * - Initialize UI and bind user events to the ViewModel.
 * - Coordinate effect handling and system interactions (e.g., ringtone picker).
 * - Validate required system permissions before saving or updating alarms.
 * - Support both creation of new alarms and editing existing alarms.
 *
 * Lifecycle notes:
 * - Delegates use [viewLifecycleOwner] to automatically clean up observers when the view is destroyed.
 * - Permissions are registered in [onAttach] to ensure readiness before the view is created.
 */
@AndroidEntryPoint
class AlarmEditorHomeFragment : Fragment() {

    companion object {
        private const val TAG = "AlarmEditorHomeFragment"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

        /** Key for passing mission item holder position in the bundle */
        const val MISSION_ITEM_HOLDER_POSITION_KEY = "$PACKAGE.MISSION_ITEM_HOLDER_POSITION_KEY"
    }

    // ViewBinding reference, cleared in onDestroyView to avoid leaks
    private var _binding: FragmentAlarmEditorBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)

    // ViewModel shared with the hosting activity
    private val viewModel: AlarmEditorViewModel by activityViewModels()

    // Navigation arguments passed from the parent Activity or NavController
    private val args: AlarmEditorHomeFragmentArgs by navArgs()

    // Launcher for system ringtone picker
    private lateinit var ringtoneLauncher: ActivityResultLauncher<Intent>

    @Inject
    lateinit var permissionChecker: MyPermissionChecker
    private lateinit var permissionFlowDelegate: PermissionFlowDelegate

    // ---------------------------------------------------------------------
    // Fragment Lifecycle
    // ---------------------------------------------------------------------

    /**
     * Registers the permission delegate early in the lifecycle to ensure
     * it is ready before the Fragment reaches CREATED.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("TAG","AlarmEditorEffectHandler onAttach executed")
        registerPermissionDelegate()
    }

    /**
     * Handles one-time initialization for the fragment:
     * - Initializes the ViewModel state if this is the first creation.
     * - Registers the system ringtone picker launcher.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TAG","AlarmEditorHomeFragment onCreate executed")


        if (savedInstanceState == null) {
            viewModel.handleSystemEvent(
                AlarmEditorSystemEvent.InitializeAlarmEditorState(args.existingAlarmId)
            )
        }

        registerRingtoneLauncher()
    }

    /**
     * Inflates the view using ViewBinding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmEditorBinding.inflate(inflater, container, false)
        Log.d("TAG","AlarmEditorHomeFragment onCreateView executed")
        return binding.root
    }

    /**
     * Sets up the UI and effect delegates, and configures the save/update button.
     * Delegates observe [viewLifecycleOwner] to automatically remove observers
     * when the view is destroyed.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("TAG","AlarmEditorHomeFragment onViewCreated executed")

        // Delegate 1: UI Rendering & Listeners
        val uiBinder = AlarmEditorUiBinder(
            binding = binding,
            viewModel = viewModel,
            lifecycleOwner = viewLifecycleOwner
        ) { event -> viewModel.handleUserEvent(event) }

        // Delegate 2: Transient Side-Effects (navigation, toasts, ringtone picker)
        AlarmEditorEffectHandler(
            fragment = this,
            uiBinder = uiBinder,
            viewModel = viewModel,
            lifecycleOwner = viewLifecycleOwner,
            ringtoneLauncher = ringtoneLauncher
        )

        // Configure the save/update action button
        setupSaveButton(uiBinder)
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG","AlarmEditorHomeFragment onResume executed")
    }

    override fun onPause() {
        super.onPause()
        Log.d("TAG","AlarmEditorHomeFragment onPause executed")
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG","AlarmEditorHomeFragment onStop executed")
    }

    /**
     * Cleans up ViewBinding to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TAG","AlarmEditorHomeFragment onDestroyView executed")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG","AlarmEditorEffectHandler onDestroy executed")
    }



    /**
     * Configures the primary action button depending on creation or update mode.
     * Calls permission checks before triggering save/update event.
     */
    private fun setupSaveButton(uiBinder: AlarmEditorUiBinder) {
        binding.saveOrUpdateAlarmBtn.apply {
            text = getString(if (args.existingAlarmId != 0) R.string.update else R.string.save)
            setOnClickListener {
                uiBinder.removeLabelFocus()
                checkPermissionAndSaveAlarm()
            }
        }
    }

    // ---------------------------------------------------------------------
    // Activity Result & Permissions
    // ---------------------------------------------------------------------

    /**
     * Registers the system ringtone picker launcher using Activity Result API.
     * Must be done during onCreate or earlier.
     */
    private fun registerRingtoneLauncher() {
        ringtoneLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.getParcelableExtraCompat<Uri>(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                )
                uri?.let {
                    viewModel.handleUserEvent(
                        AlarmEditorUserEvent.SoundEvent.RingtoneSelected(it.toString())
                    )
                }
            }
        }
    }

    /**
     * Prepares the permission engine to handle runtime and special permissions:
     * - Post-Notifications
     * - Exact Alarms
     * - Full-Screen Intents
     */
    private fun registerPermissionDelegate() {
        val requester = MyAppPermissionRequester(
            caller = this,
            lifecycle = lifecycle,
            checker = permissionChecker,
            type = RequesterType.BOTH
        )
        permissionFlowDelegate = PermissionFlowDelegate(this, permissionChecker, requester)
    }

    /**
     * Validates required system permissions before committing the alarm.
     * Triggers the ViewModel save/update action if all permissions are granted.
     */
    private fun checkPermissionAndSaveAlarm() {
        val alarmRequirements = listOf(
            Requirement(
                permission = MyAppPermission.Runtime.PostNotifications,
                rationaleTitle = getString(R.string.alarm_notification_permission_rationale_title),
                rationaleMessage = getString(R.string.alarm_notification_permission_rationale_message),
                toastOnDeny = getString(R.string.alarm_notification_permission_denied_toast),
                permanentlyDeniedTitle = getString(R.string.alarm_notification_permission_permanently_denied_title),
                permanentlyDeniedMessage = getString(R.string.alarm_notification_permission_permanently_denied_message),
                feature = AppFeature.ALARM,
            ),
            Requirement(
                permission = MyAppPermission.Special.ScheduleExactAlarms,
                rationaleTitle = getString(R.string.alarm_exact_alarm_permission_rationale_title),
                rationaleMessage = getString(R.string.alarm_exact_alarm_permission_rationale_message),
                toastOnDeny = getString(R.string.alarm_exact_alarm_permission_denied_toast),
                feature = AppFeature.ALARM
            ),
            Requirement(
                permission = MyAppPermission.Special.FullScreenIntent,
                rationaleTitle = getString(R.string.alarm_full_screen_permission_rationale_title),
                rationaleMessage = getString(R.string.alarm_full_screen_permission_rationale_message),
                toastOnDeny = getString(R.string.alarm_full_screen_permission_denied_toast),
                feature = AppFeature.ALARM,
            )
        )

        permissionFlowDelegate.run(alarmRequirements) {
            viewModel.handleUserEvent(AlarmEditorUserEvent.ActionEvent.SaveOrUpdate)
        }
    }

}