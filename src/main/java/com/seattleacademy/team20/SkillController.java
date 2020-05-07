package com.seattleacademy.team20;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SkillController {
	private static final Logger logger = LoggerFactory.getLogger(SkillController.class);
//requestMappingアノテーションで、特定のURLリクエストに対してマッピングを行う。
	@RequestMapping(value = "/skillUpload", method = RequestMethod.GET)
	public String skillUpload(Locale locale,Model model) {
		logger.info("Welcome SkillUpload The client locale is {}", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG,locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("severTime",formattedDate);
		//view名を返す
		return "skillUpload";
	}

}
