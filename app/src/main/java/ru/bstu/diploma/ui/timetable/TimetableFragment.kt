package ru.bstu.diploma.ui.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.combine
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentTimetableBinding
import java.lang.reflect.TypeVariable

class TimetableFragment : Fragment() {
    private lateinit var binding: FragmentTimetableBinding

    private lateinit var selectedPeriod: String
    private lateinit var selectedPeriodValue: String

    private lateinit var selectedGroup: String

    private lateinit var selectedProfessor: String
    private lateinit var selectedProfessorValue: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimetableBinding.inflate(inflater)
        setHasOptionsMenu(true)

        binding.btnChooseTimetable.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
                setContentView(R.layout.dialog_bottom_sheet)
                val bottomSheet: FrameLayout = findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
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

                spinnerPeriod.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedPeriod = parent?.getItemAtPosition(position).toString()
                        selectedPeriodValue = resources.getStringArray(R.array.periods_values)[position]
                    }
                }

                spinnerGroup.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedGroup = parent?.getItemAtPosition(position).toString()
                        spinnerProfessor.isEnabled = selectedGroup == "Выберите группу"
                    }
                }

                spinnerProfessor.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedProfessor = parent?.getItemAtPosition(position).toString()
                        selectedProfessorValue = resources.getStringArray(R.array.professors_values)[position]
                        spinnerGroup.isEnabled = selectedProfessor == "Выберите преподавателя"
                    }

                }

                show()

                btnShowTimetable!!.setOnClickListener {

                    dismiss()
                }
            }
        }

        val html = "<table class='contless'><tr><td class=\"daeweek\" colspan=\"4\">Пятница</td></tr><tr><td class=\"itmles schtime\" rowspan=2><br>11:30 - 13:05</td><td class=\"itmles schname\"></td><td class=\"itmles schteacher\"></td><td class=\"itmles schclass\"></td></tr><tr><td class=\"itmles schname\">Инновационный менеджмент<br><span class=\"schtype\">практическое занятие</span></td><td class=\"itmles schteacher\"><a href=\"/education/schedule/?form=очная&period=2019-2020_2_1&group=О-15-ИАС-аид-С\">О-15-ИАС-аид-С.</a></td><td class=\"itmles schclass\">ауд. 239</td></tr><tr><td class=\"itmles schtime\"><br>13:20 - 14:55</td><td class=\"itmles schname\">Инновационный менеджмент<br><span class=\"schtype\">лекция</span></td><td class=\"itmles schteacher\"><a href=\"/education/schedule/?form=очная&period=2019-2020_2_1&group=О-15-ИАС-аид-С\">О-15-ИАС-аид-С.</a></td><td class=\"itmles schclass\">ауд. 220</td></tr><tr><td class=\"daeweek\" colspan=\"4\">Суббота</td></tr><tr><td class=\"itmles schtime\"><br>09:45 - 11:20</td><td class=\"itmles schname\">Производственная практика (научно-исследовательская работа)<br><span class=\"schtype\">практическое занятие</span></td><td class=\"itmles schteacher\"><a href=\"/education/schedule/?form=очная&period=2019-2020_2_1&group=О-19-ИН-уип-М\">О-19-ИН-уип-М.</a><br><a href=\"/education/schedule/?form=очная&period=2019-2020_2_1&group=О-19-ИСТ-истпо-М\">О-19-ИСТ-истпо-М.</a><br><a href=\"/education/schedule/?form=очная&period=2019-2020_2_1&group=О-19-ИСТ-сапр-М\">О-19-ИСТ-сапр-М.</a></td><td class=\"itmles schclass\">ауд. каф.</td></tr><tr><td class=\"itmles schtime\" rowspan=2><br>11:30 - 13:05</td><td class=\"itmles schname\">Производственная практика (научно-исследовательская работа)<br><span class=\"schtype\">практическое занятие</span></td><td class=\"itmles schteacher\"><a href=\"/education/schedule/?form=очная&period=2019-2020_2_1&group=О-19-ИН-уип-М\">О-19-ИН-уип-М.</a><br><a href=\"/education/schedule/?form=очная&period=2019-2020_2_1&group=О-19-ИСТ-истпо-М\">О-19-ИСТ-истпо-М.</a><br><a href=\"/education/schedule/?form=очная&period=2019-2020_2_1&group=О-19-ИСТ-сапр-М\">О-19-ИСТ-сапр-М.</a></td><td class=\"itmles schclass\">ауд. каф.</td></tr><tr><td class=\"itmles schname\"></td><td class=\"itmles schteacher\"></td><td class=\"itmles schclass\"></td></tr></table><table class='contless'></table>"

        val data =
            "<html>\n" +
                    "<head>\n" +
                    "<style> \n" +
                    ".contless {width: 100%;border-collapse: collapse;}\n" +
                    "\t.daeweek{padding:30px 0 10px;font-size: 20px; border-bottom: solid 1px e0dfdf;}\n" +
                    "\t.itmles {padding: 25px 10px;vertical-align: middle;border-bottom: solid 1px e0dfdf;}\n" +
                    "\t\t.schtype{font-style: italic;color: de3e3e;}\n" +
                    "\ttd.itmles.schteacher {min-width: 110px;}\n" +
                    "\t.schtime{background-color: eef2f7;text-align: center;min-width:110px;border-left: solid 1px e0dfdf;}\n" +
                    "\t.schclass{border-right: solid 1px e0dfdf;min-width: 80px;}\n" +
                    ".nameusr{font-weight: bold;}\n" +
                    ".itmfilter{padding-bottom: 20px;}\n" +
                    ".btnprint {cursor: pointer;width: 30px;height: 30px;background:url(/upload/img/print.png);background-size: 100%;position: absolute;right: 15px;top: 40px;}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    html +
                    "</body>\n" +
                    "</html>"

        with(binding.webView){
            setInitialScale(150)
            loadData(data,"text/html", "UTF-8" )
        }

        return binding.root
    }
}