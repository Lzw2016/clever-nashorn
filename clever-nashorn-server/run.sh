########################JVM Setting#####################################################
project_name="workbench-"${NODE_FILE_CONFIG%%.*}
java_mem=${JAVA_MEM:-"3g"}
java_mem_xmn=${JAVA_MEM_XMN:-"512m"}
mem_g_with_mb=1024
java_mem_ratio_up=${JAVA_MEM_RATIO_UP:-1};java_mem_ratio_down=${JAVA_MEM_RATIO_DOWN:-6}

if [ ! $JAVA_MEM_XMN ];then
 if [[ ${java_mem,,} =~ 'g' ]];then
   java_mem_g_val=${java_mem%%g*}
   java_mem_xmn=`expr $java_mem_g_val \* $mem_g_with_mb`
 else
   java_mem_xmn=${java_mem%%m*}
 fi
 java_mem_xmn=`expr $java_mem_xmn \* $java_mem_ratio_up / $java_mem_ratio_down`'m'
 echo $java_mem_xmn;
fi
########################################################################################
DATABASE_OPTS=" -Ddatabase.codeset=ISO-8859-1 -Ddatabase.logging=false "

URI_ENCODING=" -Dorg.eclipse.jetty.util.URI.charset=UTF-8 "

JAVA_MEM_OPTS=" -DappName=${prokect_name} -server -Xmx${java_mem} -Xms${java_mem} -Xmn${java_mem_xmn} -XX:PermSize=128m -Xss512k -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19  -Xnoclassgc -XX:+UseParNewGC -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSPermGenSweepingEnabled -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:-CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:+UseCompressedOops -XX:-HeapDumpOnOutOfMemoryError"

JAVA_OPTS_EXT=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dapplication.codeset=UTF-8 -Dfile.encoding=UTF-8 "
########################################################################################

java $JAVA_MEM_OPTS $DATABASE_OPTS $URI_ENCODING $JAVA_OPTS_EXT -jar app.jar \
 --spring.profiles.active=${PROFILES_ACTIVE:-"dev"} --server.port=${SERVER_PORT:-8080} \
 ${PROGRAM_EXT_ARG}
