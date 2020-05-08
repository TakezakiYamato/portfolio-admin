package com.seattleacademy.team20;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Controller
public class SkillController {
	private static final Logger logger = LoggerFactory.getLogger(SkillController.class);
	@Autowired
	//Mysql接続
	private JdbcTemplate jdbcTemplate;

	//requestMappingアノテーションで、特定のURLリクエストに対してマッピングを行う。
	@RequestMapping(value = "/skillUpload", method = RequestMethod.GET)
	public String skillUpload(Locale locale, Model model) throws IOException {
		logger.info("Welcome Skillupload! The client locale is {}.", locale);

		initialize();
		List<Skill> skills = selectSkills();
		uploadSkill(skills);

		return "skillUpload";
	}

	public List<Skill> selectSkills() {
		final String sql = "select * from skills";
		return jdbcTemplate.query(sql, new RowMapper<Skill>() {
			public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Skill(rs.getString("category"),
						rs.getString("name"), rs.getInt("score"));
			}
		});
	}

	private FirebaseApp app;

	public void initialize() throws IOException {
		FileInputStream refreshToken = new FileInputStream(
				"/Users/takezakigakuto/Downloads/dev-portfolio-5ed3a-firebase-adminsdk-zqots-4a1b8341ea.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(refreshToken))
				.setDatabaseUrl("https://dev-portfolio-5ed3a.firebaseio.com/")
				.build();
		app = FirebaseApp.initializeApp(options);
	}

	public void uploadSkill(List<Skill> skills) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("skills");
		/*MapでSQLと対応づけを行う
		 * Listは値を貯める操作
		 */
		//Rowmapperを引数に置く事で、maprowメソッドでdatebaseの中身を呼び出す
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		//category単位でデータを取り出す
		Map<String, List<Skill>> skillMap = skills.stream().collect(Collectors.groupingBy(Skill::getCategory));
		for (Map.Entry<String, List<Skill>> entry : skillMap.entrySet()) {
			map = new HashMap<>();
			//            System.out.println(entry.getKey());
			//            System.out.println(entry.getValue());
			map.put("name", entry.getKey());
			map.put("skill", entry.getValue());

			dataList.add(map);
		}
		//        map = new HashMap<>();
		//        map.put("category", skill.getCategory());
		//        Map<String, List<Skill>> skillMap = skills.stream().collect(Colletors.groupingBy(Skill::getCategory));
		//        dataList.add(map);

		ref.setValue(dataList, new DatabaseReference.CompletionListener() {
			@Override
			public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
				if (databaseError != null) {
					System.out.println("Data could not be served" + databaseError.getMessage());
				} else {
					System.out.println("Data  served successfully.");
				}
			}
		});
	}

	//Skillsオブジェクトを作成
	public class Skill {
		private String category;
		private String name;
		private int score;

		//コンストラクタにインスタンス生成時に引数付与する処理を書く
		public Skill(String category, String name, int score) {
			this.category = category;
			this.name = name;
			this.score = score;
		}

		public String getCategory() {
			return category;
		}

		public String getName() {
			return name;
		}

		public int getScore() {
			return score;
		}
	}
}
