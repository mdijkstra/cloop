/************************************************************
* populate validation tables
************************************************************/

# alert types: critical, warning(action), info
# log_types (levels): error, warning, info, debug (probably not use)
# automode_switches: off, lowsOnly, fullOn


insert into alert_types (type, description) values
('critical', 'Important alert: low bg and extremely low bg, multiple sync failure'),
('warning', 'Warning alert or take action: potential low, high, need injection or temp'),
('info', 'Info that some action was taken: injection given, periodic bg');

insert into log_types (type, description) values
('error','Errors'),
('warning','Warnings'),
('info','Info'),
('debug','probably not used');

insert into automode_switches (is_on, description) values
('off','The system will not bolus or set temps'),
('lowsOnly','The system will only sent temp rates and will not bolus'),
('fullOn','The system will bolus and set temp rates');

