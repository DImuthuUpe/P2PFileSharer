namespace java node

service node{
    oneway void search(1:string ip, 2:i32 port, 3:string query, 4:i32 hops),

    oneway void searchResponse(1:string ip, 2:i32 port, 3:string query, 4:i32 hops, 5:list<string> files)

}