package io.github.rlshep.bjcp2015beerstyles.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import io.github.rlshep.bjcp2015beerstyles.BjcpActivity;
import io.github.rlshep.bjcp2015beerstyles.domain.Category;
import io.github.rlshep.bjcp2015beerstyles.domain.SearchResult;
import io.github.rlshep.bjcp2015beerstyles.domain.Section;
import io.github.rlshep.bjcp2015beerstyles.domain.Tag;
import io.github.rlshep.bjcp2015beerstyles.domain.VitalStatistics;
import io.github.rlshep.bjcp2015beerstyles.helpers.PreferencesHelper;

import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpConstants.DATABASE_VERSION;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpConstants.DEFAULT_LANGUAGE;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_ABV_END;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_ABV_START;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_BODY;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_BOOKMARKED;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_CATEGORY_CODE;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_CAT_ID;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_FG_END;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_FG_START;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_HEADER;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_IBU_END;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_IBU_START;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_ID;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_LANG;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_LEFT;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_NAME;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_OG_END;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_OG_START;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_ORDER;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_PARENT_ID;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_RESULT_ID;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_REVISION;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_RIGHT;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_SRM_END;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_SRM_START;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_TABLE_NAME;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.COLUMN_TAG;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.TABLE_CATEGORY;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.TABLE_FTS_SEARCH;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.TABLE_SECTION;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.TABLE_SYNONYMS;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.TABLE_TAG;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpContract.TABLE_VITALS;

public class BjcpDataHelper extends BaseDataHelper {
    private static BjcpDataHelper instance;
    private PreferencesHelper ph;

    private static final String CATEGORY_SELECT = "SELECT " + COLUMN_ID + ", " + COLUMN_CATEGORY_CODE + ", " + COLUMN_NAME + ", " + COLUMN_ORDER + ", " + COLUMN_PARENT_ID + "," + COLUMN_BOOKMARKED + "," + COLUMN_REVISION + "," + COLUMN_LANG + " FROM " + TABLE_CATEGORY + " C ";
    private static final String CATEGORY_TOP_WHERE = " WHERE " + COLUMN_REVISION + " = " + Category.CURRENT_REVISION;

    protected BjcpDataHelper(BjcpActivity activity) { 
        super(activity);
        ph = new PreferencesHelper(activity);
    }

    // Use the application context, which will ensure that you don't accidentally leak an Activity's context. See this article for more information: http://bit.ly/6LRzfx
    public static synchronized BjcpDataHelper getInstance(BjcpActivity activity) {
        instance = new BjcpDataHelper(activity);

        return instance;
    }

    public Category getCategory(String categoryId) {
        String query = CATEGORY_SELECT + " WHERE " + COLUMN_ID + " = " + categoryId;
        return getCategories(query).get(0);
    }

    public List<Category> getAllCategories() {
        String query = CATEGORY_SELECT + CATEGORY_TOP_WHERE + " AND " + COLUMN_PARENT_ID + " IS NULL " + " AND " + COLUMN_LANG + " = '" + ph.getLanguage() + "'ORDER BY " + COLUMN_ORDER;
        return getCategories(query);
    }

    public List<Category> getCategoriesByIds(List<Long> ids) {
        List<Category> categories = new ArrayList<>();
        String sql;

        if (null != ids && !ids.isEmpty()) {
            sql = CATEGORY_SELECT + " WHERE " + COLUMN_ID + " IN(" + getIdsQuery(ids) + ")";
            categories = getCategories(sql);
        }

        return categories;
    }

    private String getIdsQuery(List<Long> ids) {
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < ids.size(); i++) {
            if (0 < i) {
                query.append(", ");
            }
            query.append(ids.get(i));
        }

        return query.toString();
    }

    private List<Category> getCategories(String query) {
        List<Category> categories = new ArrayList<Category>();
        Category category;

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_ID)) != null) {
                category = new Category();
                category.setId(c.getLong(c.getColumnIndex(COLUMN_ID)));
                category.setCategoryCode(c.getString(c.getColumnIndex(COLUMN_CATEGORY_CODE)));
                category.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
                category.setOrderNumber(c.getInt(c.getColumnIndex(COLUMN_ORDER)));
                category.setParentId(c.getLong(c.getColumnIndex(COLUMN_PARENT_ID)));
                category.setBookmarked(c.getInt(c.getColumnIndex(COLUMN_BOOKMARKED)) > 0);
                category.setRevision(c.getString(c.getColumnIndex(COLUMN_REVISION)));
                category.setLanguage((c.getString(c.getColumnIndex((COLUMN_LANG)))));
                categories.add(category);

            }
            c.moveToNext();
        }

        c.close();

        return categories;
    }

    public List<Section> getCategorySections(String categoryId) {
        List<Section> sections = new ArrayList<Section>();
        Section section;

        String query = "SELECT S." + COLUMN_ID + ", S." + COLUMN_BODY + ", S." + COLUMN_HEADER + " FROM " + TABLE_SECTION + " S WHERE S." + COLUMN_CAT_ID + " = " + categoryId + " ORDER BY S." + COLUMN_ORDER;

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            section = new Section();
            if (c.getString(c.getColumnIndex(COLUMN_ID)) != null) {
                section.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                section.setBody(c.getString(c.getColumnIndex(COLUMN_BODY)));
            }
            c.moveToNext();
            sections.add(section);
        }

        c.close();

        return sections;
    }

    public List<Category> getCategoriesByParent(long parentId) {
        return getCategoriesByParent((Long.valueOf(parentId)).toString());
    }

    public List<Category> getCategoriesByParent(String parentId) {
        String query = CATEGORY_SELECT + " WHERE " + COLUMN_PARENT_ID + " = " + parentId;
        return getCategories(query);
    }

    public List<Category> getBookmarkedCategories() {
        String query = CATEGORY_SELECT + CATEGORY_TOP_WHERE + " AND " + COLUMN_BOOKMARKED + " = 1 AND " + COLUMN_LANG + " = '" + ph.getLanguage() + "' ORDER BY " + COLUMN_CATEGORY_CODE;

        return getCategories(query);
    }

    public List<VitalStatistics> getVitalStatistics(String categoryId) {
        List<VitalStatistics> vitalStatisticses = new ArrayList<>();
        VitalStatistics vitalStatistics;

        String query = "SELECT V." + COLUMN_ID + ", V." + COLUMN_OG_START + ", V." + COLUMN_OG_END + ", V." + COLUMN_FG_START + ", V." + COLUMN_FG_END + ", V." + COLUMN_IBU_START + ", V." + COLUMN_IBU_END + ", V." + COLUMN_SRM_START + ", V." + COLUMN_SRM_END + ", V." + COLUMN_ABV_START + ", V." + COLUMN_ABV_END + ", " + COLUMN_HEADER + " FROM " + TABLE_VITALS + " V WHERE V." + COLUMN_CAT_ID + " = " + categoryId;

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            vitalStatistics = new VitalStatistics();

            if (c.getString(c.getColumnIndex(COLUMN_ID)) != null) {
                vitalStatistics.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                vitalStatistics.setOgStart(c.getDouble(c.getColumnIndex(COLUMN_OG_START)));
                vitalStatistics.setOgEnd(c.getDouble(c.getColumnIndex(COLUMN_OG_END)));
                vitalStatistics.setFgStart(c.getDouble(c.getColumnIndex(COLUMN_FG_START)));
                vitalStatistics.setFgEnd(c.getDouble(c.getColumnIndex(COLUMN_FG_END)));
                vitalStatistics.setIbuStart(c.getInt(c.getColumnIndex(COLUMN_IBU_START)));
                vitalStatistics.setIbuEnd(c.getInt(c.getColumnIndex(COLUMN_IBU_END)));
                vitalStatistics.setSrmStart(c.getDouble(c.getColumnIndex(COLUMN_SRM_START)));
                vitalStatistics.setSrmEnd(c.getDouble(c.getColumnIndex(COLUMN_SRM_END)));
                vitalStatistics.setAbvStart(c.getDouble(c.getColumnIndex(COLUMN_ABV_START)));
                vitalStatistics.setAbvEnd(c.getDouble(c.getColumnIndex(COLUMN_ABV_END)));
                vitalStatistics.setHeader(c.getString(c.getColumnIndex(COLUMN_HEADER)));
            }
            c.moveToNext();
            vitalStatisticses.add(vitalStatistics);
        }

        c.close();

        return vitalStatisticses;
    }

    public void updateCategoryBookmarked(Category category) {
        List<Category> categories = new ArrayList<Category>();
        categories.add(category);

        updateCategoriesBookmarked(categories);
    }

    public void updateCategoriesBookmarked(List<Category> categories) {
        ContentValues cv = new ContentValues();

        for (Category category : categories) {
            cv.put(COLUMN_BOOKMARKED, (category.isBookmarked() ? 1 : 0));
            getWrite().update(TABLE_CATEGORY, cv, (COLUMN_ID + " = " + category.getId()), null);
        }
    }

    public List<SearchResult> search(String keyword) {
        List<SearchResult> searchResults = new ArrayList<>();
        List<String> keywords = searchSynonyms(keyword);

        if (keywords.isEmpty()) {
            keywords.add(keyword);
        }

        for (String k : keywords) {
            searchResults.addAll(searchStyles(k));

            if (!DEFAULT_LANGUAGE.equals(ph.getLanguage())) {
                searchResults.addAll(searchStylesDefaultLanguage(k));
            }
        }

        return searchResults;
    }

    public List<String> searchSynonyms(String keyword) {
        ArrayList<String> searchResults = new ArrayList<>();
        String searchResult;
        String query = "SELECT " + COLUMN_RIGHT + " FROM " + TABLE_SYNONYMS + " WHERE  " + COLUMN_LANG + " = '" + ph.getLanguage() + "' AND UPPER(" + COLUMN_LEFT + ") = UPPER('" + keyword + "')";

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);

        while (c.moveToNext()) {
            if (c.getString(c.getColumnIndex(COLUMN_RIGHT)) != null) {
                searchResult = c.getString(c.getColumnIndex(COLUMN_RIGHT));
                searchResults.add(searchResult);
            }
        }

        c.close();

        return searchResults;
    }


    private List<SearchResult> searchStyles(String keyword) {
        SearchResult searchResult;
        List<SearchResult> searchResults = new ArrayList<>();
        String query = "SELECT " + COLUMN_RESULT_ID + ", " + COLUMN_TABLE_NAME + " FROM " + TABLE_FTS_SEARCH + " WHERE " + COLUMN_LANG + " = '" + ph.getLanguage() + "' AND " + COLUMN_REVISION + " = " + Category.CURRENT_REVISION + " AND " + COLUMN_BODY + " MATCH '\"" + keyword + "\"*' ORDER BY " + COLUMN_RESULT_ID;

        Cursor c = getRead().rawQuery(query, null);

        while (c.moveToNext()) {
            if (c.getString(c.getColumnIndex(COLUMN_RESULT_ID)) != null) {
                searchResult = new SearchResult();
                searchResult.setResultId(c.getInt(c.getColumnIndex(COLUMN_RESULT_ID)));
                searchResult.setTableName(c.getString(c.getColumnIndex(COLUMN_TABLE_NAME)));
                searchResult.setQuery(keyword);

                searchResults.add(searchResult);
            }
        }

        c.close();

        return searchResults;
    }

    private List<SearchResult> searchStylesDefaultLanguage(String keyword) {
        SearchResult searchResult;
        List<SearchResult> searchResults = new ArrayList<>();
        String query = "SELECT C2." + COLUMN_ID + ", FS." + COLUMN_TABLE_NAME + " FROM " + TABLE_FTS_SEARCH + " FS JOIN " + TABLE_CATEGORY + " C1 ON C1." + COLUMN_ID + " = FS." + COLUMN_RESULT_ID + " JOIN " + TABLE_CATEGORY + " C2 ON C2." + COLUMN_CATEGORY_CODE + " = C1." + COLUMN_CATEGORY_CODE + " AND C2." + COLUMN_LANG + " = '" + ph.getLanguage() + "' WHERE FS." + COLUMN_LANG + " = '" + DEFAULT_LANGUAGE + "' AND FS." + COLUMN_REVISION + " = " + Category.CURRENT_REVISION + " AND FS." + COLUMN_BODY + " MATCH '\"" + keyword + "\"*' ORDER BY C2." + COLUMN_ID;

        Cursor c = getRead().rawQuery(query, null);

        while (c.moveToNext()) {
            if (c.getString(c.getColumnIndex(COLUMN_ID)) != null) {
                searchResult = new SearchResult();
                searchResult.setResultId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                searchResult.setTableName(c.getString(c.getColumnIndex(COLUMN_TABLE_NAME)));
                searchResult.setQuery(keyword);

                searchResults.add(searchResult);
            }
        }

        c.close();

        return searchResults;
    }
    public List<String> getAllSynonyms() {
        ArrayList<String> synonyms = new ArrayList<>();
        final String query = "SELECT DISTINCT " + COLUMN_LEFT + " FROM " + TABLE_SYNONYMS + " WHERE " + COLUMN_LANG + " = '" + ph.getLanguage() + "'";

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);

        while (c.moveToNext()) {
            if (c.getString(c.getColumnIndex(COLUMN_LEFT)) != null) {
                synonyms.add(c.getString(c.getColumnIndex(COLUMN_LEFT)));
            }
        }

        c.close();

        return synonyms;
    }

    public List<String> getAllCategoryNames() {
        ArrayList<String> names = new ArrayList<>();
        String language = ph.getLanguage();

        String query = "SELECT DISTINCT " + COLUMN_NAME + " FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_LANG + " IN ('" + language + "'";

        if (!DEFAULT_LANGUAGE.equals(language)) {
            query += ", '" + DEFAULT_LANGUAGE + "'";
        } else {

        }
        query += ") ORDER BY " + COLUMN_NAME;

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_NAME)) != null) {
                names.add(c.getString(c.getColumnIndex(COLUMN_NAME)));
            }
            c.moveToNext();
        }

        c.close();

        return names;
    }

    public boolean isCorrectDatabaseVersion() {
        return (DATABASE_VERSION == getRead().getVersion());
    }

    public String getSearchVitalStatisticsQuery(VitalStatistics vitalStatistics) {
        return "SELECT V." + COLUMN_CAT_ID + " FROM " + TABLE_VITALS + " V JOIN " + TABLE_CATEGORY + " C ON C." + COLUMN_ID + " = V." + COLUMN_CAT_ID + " WHERE V." + COLUMN_IBU_START + ">=" + vitalStatistics.getIbuStart() + " AND V." + COLUMN_IBU_END + "<=" + vitalStatistics.getIbuEnd() + " AND V." + COLUMN_SRM_START + ">=" + vitalStatistics.getSrmStart() + " AND V." + COLUMN_SRM_END + "<=" + vitalStatistics.getSrmEnd() + " AND V." + COLUMN_ABV_START + ">=" + vitalStatistics.getAbvStart() + " AND V." + COLUMN_ABV_END + "<=" + vitalStatistics.getAbvEnd() + " AND C." + COLUMN_LANG + " = '" + ph.getLanguage() + "' ORDER BY C." + COLUMN_ORDER;
    }

    public List<SearchResult> searchVitals(String query) {
        SearchResult searchResult;
        List<SearchResult> searchResults = new ArrayList<>();

        Cursor c = getRead().rawQuery(query, null);

        while (c.moveToNext()) {
            if (c.getString(c.getColumnIndex(COLUMN_CAT_ID)) != null) {
                searchResult = new SearchResult();
                searchResult.setResultId(c.getInt(c.getColumnIndex(COLUMN_CAT_ID)));
                searchResult.setTableName(TABLE_CATEGORY);

                searchResults.add(searchResult);
            }
        }

        c.close();

        return searchResults;
    }

    public List<Tag> getTags(String categoryId) {
        List<Tag> tags = new ArrayList<>();
        Tag tag;

        final String query = "SELECT T." + COLUMN_ID + ", T." + COLUMN_CAT_ID + ", T." + COLUMN_TAG + " FROM " + TABLE_TAG + " T WHERE T." + COLUMN_CAT_ID + " = " + categoryId;

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            tag = new Tag();
            if (c.getString(c.getColumnIndex(COLUMN_ID)) != null) {
                tag.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                tag.setCategoryId(c.getColumnIndex(COLUMN_CAT_ID));
                tag.setTag(c.getString(c.getColumnIndex(COLUMN_TAG)));
            }
            c.moveToNext();
            tags.add(tag);
        }

        c.close();

        return tags;
    }

    public List<String> getAllTags() {
        ArrayList<String> tags = new ArrayList<>();
        final String query = "SELECT DISTINCT T." + COLUMN_TAG + " FROM " + TABLE_TAG + " T JOIN " + TABLE_CATEGORY + " C ON C." + COLUMN_ID + " = T." + COLUMN_CAT_ID + " WHERE C." + COLUMN_LANG + " = '" + ph.getLanguage() + "'";

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);

        while (c.moveToNext()) {
            if (c.getString(c.getColumnIndex(COLUMN_TAG)) != null) {
                tags.add(c.getString(c.getColumnIndex(COLUMN_TAG)));
            }
        }

        c.close();

        return tags;
    }

    public List<String> getAllLanguages() {
        ArrayList<String> languages = new ArrayList<>();
        final String query = "SELECT DISTINCT " + COLUMN_LANG + " FROM " + TABLE_CATEGORY;

        //Cursor point to a location in your results
        Cursor c = getRead().rawQuery(query, null);

        while (c.moveToNext()) {
            if (c.getString(c.getColumnIndex(COLUMN_LANG)) != null) {
                languages.add(c.getString(c.getColumnIndex(COLUMN_LANG)));
            }
        }

        c.close();

        return languages;
    }
}
