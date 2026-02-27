package com.example.smartalarm.feature.alarm.presentation.view.fragment.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.permission.MyPermissionChecker
import com.example.smartalarm.core.framework.permission.PermissionFlowDelegate
import com.example.smartalarm.core.framework.permission.model.AppFeature
import com.example.smartalarm.core.framework.permission.model.MyAppPermission
import com.example.smartalarm.core.framework.permission.MyAppPermissionRequester
import com.example.smartalarm.core.framework.permission.model.RequesterType
import com.example.smartalarm.core.framework.permission.model.Requirement
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.exception.asUiText
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.databinding.FragmentAlarmBinding
import com.example.smartalarm.feature.alarm.framework.services.AlarmService
import com.example.smartalarm.feature.alarm.presentation.adapter.AlarmAdapter
import com.example.smartalarm.feature.alarm.presentation.effect.home.AlarmEffect
import com.example.smartalarm.feature.alarm.presentation.event.home.AlarmEvent
import com.example.smartalarm.feature.alarm.presentation.uiState.AlarmUiState
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmEditorActivity
import com.example.smartalarm.feature.alarm.presentation.viewmodel.home.AlarmViewModel
import com.example.smartalarm.feature.alarm.utility.enableSwipeToDelete
import com.example.smartalarm.feature.alarm.utility.showSnackBarWithUndo
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AlarmFragment : Fragment() {


    companion object {

        // Tag used for logging in AlarmFragment
        private const val TAG = "AlarmFragment"

        // Error message displayed when view binding reference is null
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

    }


    // ---------------------------------------------------------------------
    // 1] Global Fields Section
    // ---------------------------------------------------------------------

    // Nullable reference to the AlarmFragment's view binding
    private var _binding: FragmentAlarmBinding? = null

    // Non-nullable reference to the Fragment's view binding, throws an error if _binding is null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)

    // ViewModel for managing the alarm data, scoped to this Fragment
    private val alarmViewModel: AlarmViewModel by viewModels()


    private lateinit var alarmsAdapter: AlarmAdapter
    @Inject
    lateinit var permissionChecker: MyPermissionChecker
    private lateinit var permissionFlowDelegate: PermissionFlowDelegate
    private lateinit var permissionRequester: MyAppPermissionRequester


    // ---------------------------------------------------------------------
    // 2] Alarm Fragment Lifecycle Methods
    // ---------------------------------------------------------------------

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionRequester = MyAppPermissionRequester(
            caller = this,
            lifecycle = lifecycle,
            checker = permissionChecker,
            type = RequesterType.BOTH
        )

        permissionFlowDelegate = PermissionFlowDelegate(
            fragment = this,
            checker = permissionChecker,
            requester = permissionRequester
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAddAlarmButton()
        setupRecyclerView()
        setUpUIStateObserver()
        setUpUIEffectsObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    // ---------------------------------------------------------------------
    // UI Setup
    // ---------------------------------------------------------------------

    private fun setUpAddAlarmButton() {
        binding.addAlarmBtn.setOnClickListener {
            checkAndStartAlarm()
        }
    }

    private fun setupRecyclerView() {

        alarmsAdapter = AlarmAdapter(
            onAlarmItemClick = { alarmId ->
                alarmViewModel.handleEvent(AlarmEvent.AlarmItemClicked(alarmId))
            },
            onAlarmSwitchToggle = { alarmId, isEnabled ->
                alarmViewModel.handleEvent(AlarmEvent.ToggleAlarm(alarmId, isEnabled))
            }
        )

        //val isPhone = resources.configuration.smallestScreenWidthDp < 600
        //val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        //val lm = if (isPhone && isPortrait) LinearLayoutManager(requireContext()) else GridLayoutManager(requireContext(), 2)


        binding.alarmRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alarmsAdapter
            setHasFixedSize(true)
        }.enableSwipeToDelete(requireContext()) { position ->
            val deletedAlarmId = alarmsAdapter.currentList[position].id
            alarmViewModel.handleEvent(AlarmEvent.AlarmItemSwiped(deletedAlarmId))
        }
    }


    // ---------------------------------------------------------------------
    // Observers Methods
    // ---------------------------------------------------------------------

    private fun setUpUIStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmViewModel.uiState.collectLatest { state ->

                    binding.apply {
                        alarmProgressBar.isVisible = state is AlarmUiState.Loading
                        emptyStateGroup.isVisible = state is AlarmUiState.Empty
                        alarmRv.isVisible = state is AlarmUiState.Success
                        if (state is AlarmUiState.Success) {
                            alarmsAdapter.submitList(state.alarms)
                        }
                    }

                }
            }
        }
    }

    private fun setUpUIEffectsObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            alarmViewModel.uiEffect.collectLatest { effect ->

                when (effect) {

                    // Navigation Effects
                    is AlarmEffect.NavigateToCreateAlarmScreen -> navigateToAlarmEditor(null)
                    is AlarmEffect.NavigateToEditAlarmScreen -> navigateToAlarmEditor(effect.alarmId)

                    // Visual Effects
                    is AlarmEffect.ShowSnackBarMessage -> showUndoSnackBar()
                    is AlarmEffect.ShowToastMessage -> showToastMessage(effect.toastMessage)
                    is AlarmEffect.ShowError -> {
                        val errorMessage = effect.error.asUiText().asString(requireContext())
                        showToastMessage(errorMessage)
                    }

                    // Service Control
                    is AlarmEffect.StopAlarmService -> stopAlarmService()
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    // UI Effect Handlers Methods
    // ---------------------------------------------------------------------

    private fun navigateToAlarmEditor(alarmId: Int?) {
        val intent = Intent(requireContext(), AlarmEditorActivity::class.java).apply {
            alarmId?.let { putExtra(AlarmEditorActivity.EXISTING_ALARM_ID_KEY, it) }
        }
        startActivity(intent)
    }

    private fun showToastMessage(toastMessage: String) {
        binding.root.showToast(toastMessage)
    }

    private fun showUndoSnackBar() {
        binding.root.showSnackBarWithUndo(
            R.string.alarm_deleted,
            R.string.undo,
            Snackbar.LENGTH_SHORT
        ) { alarmViewModel.handleEvent(AlarmEvent.UndoDeletedAlarm) }
    }

    private fun stopAlarmService() {
        val intent = Intent(requireContext(), AlarmService::class.java)
        requireContext().stopService(intent)
    }


    //-------------------------------
    // Permission Handling
    //------------------------------
    private fun checkAndStartAlarm() {

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

        // Run the chain
        permissionFlowDelegate.run(alarmRequirements) {
            alarmViewModel.handleEvent(AlarmEvent.AddNewAlarm)
        }

    }

}
