select
    concat(year(u.created_at), '-', month(u.created_at)) as date,
    count(*) as count
from user u
group by year(u.created_at), month(u.created_at);

select
    concat(year(us.created_at), '-', month(us.created_at)) as date,
    count(*) as count
from user_shrine us
group by year(us.created_at), month(us.created_at);