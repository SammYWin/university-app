package ru.bstu.diploma.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentScheduleBinding
import ru.bstu.diploma.repositories.PreferencesRepository
import ru.bstu.diploma.viewmodels.ScheduleViewModel

class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding

    private lateinit var selectedPeriod: String
    private lateinit var selectedPeriodValue: String

    private lateinit var selectedGroup: String

    private lateinit var selectedProfessor: String
    private lateinit var selectedProfessorValue: String

    private lateinit var viewModel: ScheduleViewModel
    private var schedule= ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentScheduleBinding.inflate(inflater)

        initViews()
        initViewModel()

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        viewModel.getScheduleData().observe(viewLifecycleOwner, Observer {
            schedule = it
            schedule = schedule.replace(Regex("<[aA].*?>"),"<span>")
            schedule = schedule.replace(Regex("</[aA]>"),"</span>")

            val theme = PreferencesRepository.getAppTheme()
            val data =
                "<html>\n" +
                        "<head>\n" +
                        "<style> \n" +
                        "body { background-color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "white" else "101010"};}" +
                        ".contless {width: 100%;border-collapse: collapse;}\n" +
                        "\t.daeweek{color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "black" else "white"};" +
                        "           padding:30px 0 10px;font-size: 20px; border-bottom: solid 1px lightgrey;}\n" +
                        "\t.itmles {padding: 25px 10px;vertical-align: middle;border-bottom: solid 1px lightgrey; color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "black" else "white"};}\n" +
                        "\t\t.schtype{font-style: italic;color:${if(theme == AppCompatDelegate.MODE_NIGHT_NO) " de3e3e" else "2196F3"};}\n" +
                        "\ttd.itmles.schteacher {min-width: 130px;}\n" +
                        "\t.schtime{background-color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "eef2f7" else "272727"};" +
                        "text-align: center; color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "black" else "white"}; min-width:110px;border-left: solid 1px lightgrey;}\n" +
                        "\t.schclass{border-right: solid 1px lightgrey;min-width: 80px;}\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        schedule +
                        "</body>\n" +
                        "</html>"
            with(binding.webView) {
                setInitialScale(180)
                settings.domStorageEnabled = true
                loadData(data, "text/html", "UTF-8")
            }
        })
    }

    private fun initViews() {
        schedule = PreferencesRepository.getSchedule()
        schedule = schedule.replace(Regex("<[aA].*?>"),"<span>")
        schedule = schedule.replace(Regex("</[aA]>"),"</span>")

        val theme = PreferencesRepository.getAppTheme()
        val data =
            "<html>\n" +
                    "<head>\n" +
                    "<style> \n" +
                    "body { background-color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "white" else "101010"};}" +
                    ".contless {width: 100%;border-collapse: collapse;}\n" +
                    "\t.daeweek{color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "black" else "white"};" +
                    "           padding:30px 0 10px;font-size: 20px; border-bottom: solid 1px lightgrey;}\n" +
                    "\t.itmles {padding: 25px 10px;vertical-align: middle;border-bottom: solid 1px lightgrey; color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "black" else "white"};}\n" +
                    "\t\t.schtype{font-style: italic;color:${if(theme == AppCompatDelegate.MODE_NIGHT_NO) " de3e3e" else "2196F3"};}\n" +
                    "\ttd.itmles.schteacher {min-width: 130px;}\n" +
                    "\t.schtime{background-color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "eef2f7" else "272727"};" +
                                "text-align: center; color: ${if(theme == AppCompatDelegate.MODE_NIGHT_NO) "black" else "white"}; min-width:110px;border-left: solid 1px lightgrey;}\n" +
                    "\t.schclass{border-right: solid 1px lightgrey;min-width: 80px;}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    schedule +
                    "</body>\n" +
                    "</html>"
        with(binding.webView) {
            setInitialScale(180)
            settings.domStorageEnabled = true
            loadData(data, "text/html", "UTF-8")
        }

        binding.btnChooseTimetable.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
                setContentView(R.layout.dialog_bottom_sheet)
                val bottomSheet: FrameLayout =
                    findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED

                val spinnerPeriod = findViewById<Spinner>(R.id.spinner_period)
                val spinnerGroup = findViewById<Spinner>(R.id.spinner_group)
                val spinnerProfessor = findViewById<Spinner>(R.id.spinner_professor)
                val btnShowTimetable = findViewById<Button>(R.id.btn_show_timetable)

                val spinnerPeriodAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.periods,
                    android.R.layout.simple_spinner_item
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                val spinnerGroupAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.groups,
                    android.R.layout.simple_spinner_item
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                val spinnerProfessorAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.professors,
                    android.R.layout.simple_spinner_item
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

                spinnerPeriod!!.adapter = spinnerPeriodAdapter
                spinnerGroup!!.adapter = spinnerGroupAdapter
                spinnerProfessor!!.adapter = spinnerProfessorAdapter

                spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedPeriod = parent?.getItemAtPosition(position).toString()
                        selectedPeriodValue = resources.getStringArray(R.array.periods_values)[position]
                    }
                }

                spinnerGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedGroup = parent?.getItemAtPosition(position).toString()

                        if(selectedGroup != "Выберите группу"){
                            spinnerProfessor.isEnabled = false
                            selectedProfessorValue = ""
                        }else{
                            spinnerProfessor.isEnabled = true
                        }
                    }
                }

                spinnerProfessor.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            selectedProfessor = parent?.getItemAtPosition(position).toString()
                            selectedProfessorValue = selectedProfessor.replace(" ", "_")

                            if(selectedProfessor != "Выберите преподавателя"){
                                spinnerGroup.isEnabled = false
                                selectedGroup = ""
                            }else{
                                spinnerGroup.isEnabled = true
                            }
                        }

                    }

                show()

                btnShowTimetable!!.setOnClickListener {
                    viewModel.loadSchedule("schedule",
                        selectedPeriodValue,
                        selectedGroup,
                        selectedProfessorValue)
                    dismiss()
                }
            }
        }
    }
}