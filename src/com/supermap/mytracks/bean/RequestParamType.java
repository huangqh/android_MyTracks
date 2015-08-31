package com.supermap.mytracks.bean;

/**
 * Created by xuty on 2014/9/24.
 */
public class RequestParamType {
    public static final String USERNAMES="userNames";
    public static final String TAGS="tags";
    public static final String SUGGEST="suggest" ;
    public static final String SOURCETYPES="sourceTypes";   //MapSourceType
    public static final String MAPSTATUS="mapStatus";        //MapStatusType
    public static final String KEYWORDS="keywords" ;
    public static final String EPSGCODE="epsgCode";
    public static final String ORDERBY="orderBy";
    public static final String CURRENTPAGE="currentPage";
    public static final String PAGESIZE="pageSize";
    public static final String STARTTIME="startTime";
    public static final String ENDTIME="endTime";
    public static final String UPDATESTART="updateStart";
    public static final String UPDATEEND="updateEnd";
    public static final String VISITSTART="visitStart";
    public static final String VISITEND="visitEnd";
    public static final String UNIQUE="unique";
    
    public class OderBy{
        public static final String ORDERFIELD="orderField";
        public static final String ORDERTYPE="orderType";
        public class OderField{
            public static final String CREATETIME="CREATETIME";
            public static final String SOURCETYPE="SOURCETYPE";
            public static final String STATUS="STATUS";
            public static final String TITLE="TITLE";
            public static final String UPDATETIME="UPDATETIME";
            public static final String USERNAME="USERNAME";
            public static final String VISITCOUNT="VISITCOUNT";
        }
        public class OderType{
            public static final String ASC="ASC";
            public static final String DESC="DESC";
        }
    }
}
