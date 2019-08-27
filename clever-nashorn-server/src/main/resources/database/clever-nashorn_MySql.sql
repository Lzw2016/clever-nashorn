create database if not exists `clever-nashorn` default character set = utf8;
use `clever-nashorn`;


/* ====================================================================================================================
    js_code_file -- 系统JS代码
==================================================================================================================== */
create table js_code_file
(
    id                  bigint          not null        auto_increment                          comment '主键id',
    biz_type            varchar(127)    not null                                                comment '业务类型',
    group_name          varchar(127)    not null                                                comment '代码分组',
    node_type           int(1)          not null        default 1                               comment '数据类型：1-文件，2-文件夹',
    file_path           varchar(255)    not null                                                comment '上级路径',
    name                varchar(255)    not null                                                comment '文件或文件夹名称',
    js_code             mediumtext                                                              comment '脚本内容',
    description         varchar(511)                                                            comment '说明',
    create_at           datetime(3)     not null        default current_timestamp(3)            comment '创建时间',
    update_at           datetime(3)                     on update current_timestamp(3)          comment '更新时间',
    primary key (id)
) comment = '系统JS代码';
create index js_code_file_biz_type on js_code_file (biz_type);
create index js_code_file_group_name on js_code_file (group_name);
create index js_code_file_file_path on js_code_file (file_path);
create index js_code_file_name on js_code_file (name);
/*------------------------------------------------------------------------------------------------------------------------

--------------------------------------------------------------------------------------------------------------------------*/


/* ====================================================================================================================
    code_file_history -- 历史JS代码
==================================================================================================================== */
create table code_file_history
(
    id                  bigint          not null        auto_increment                          comment '主键id',
    biz_type            varchar(127)    not null                                                comment '业务类型',
    group_name          varchar(127)    not null                                                comment '代码分组',
    file_path           varchar(255)    not null                                                comment '上级路径',
    name                varchar(255)    not null                                                comment '文件或文件夹名称',
    js_code             mediumtext                                                              comment '脚本内容',
    description         varchar(511)                                                            comment '说明',
    create_at           datetime(3)     not null        default current_timestamp(3)            comment '创建时间',
    update_at           datetime(3)                     on update current_timestamp(3)          comment '更新时间',
    primary key (id)
) comment = '历史JS代码';
create index code_file_history_biz_type on code_file_history (biz_type);
create index code_file_history_group_name on code_file_history (group_name);
create index code_file_history_file_path on code_file_history (file_path);
create index code_file_history_name on code_file_history (name);
/*------------------------------------------------------------------------------------------------------------------------

--------------------------------------------------------------------------------------------------------------------------*/


/* ====================================================================================================================
    code_run_log -- JS代码运行日志
==================================================================================================================== */
create table code_run_log
(
    id                  bigint          not null        auto_increment                          comment '主键id',
    js_code_id          bigint          not null                                                comment '系统JS代码ID(nashorn_js_code.id)',
    js_code             mediumtext      not null                                                comment '脚本内容',
    run_start           datetime(3)     not null                                                comment '运行开始时间',
    run_end             datetime(3)     not null                                                comment '运行结束时间',
    run_log             mediumtext      not null                                                comment '运行日志',
    status              int(1)          not null        default 1                               comment '脚本运行状态：1-运行中，2-成功，3-异常，4-超时，...',
    create_at           datetime(3)     not null        default current_timestamp(3)            comment '创建时间',
    update_at           datetime(3)                     on update current_timestamp(3)          comment '更新时间',
    primary key (id)
) comment = '历史JS代码';
create index code_run_log_js_code_id on code_run_log (js_code_id);
create index code_run_log_run_start on code_run_log (run_start);
create index code_run_log_run_end on code_run_log (run_end);
/*------------------------------------------------------------------------------------------------------------------------

--------------------------------------------------------------------------------------------------------------------------*/






















