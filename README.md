# Spring Security And Mongodb

A quick Spring Security in-memory authentication Application that uses Mongodb as datastore.

This application was created to test how Spring security matches entered password with the one in database. 

Turns out, UserDetailsService which was injected into SpringSecurity class and 
which was given as a parameter to AuthenticationManagerBuilder.userDetailsService(..). does password matching for us.
No need for us to manually check entered password against the password stored in DB.

To check where /login endpoint was configured, look inside com.example.secure.config.WebConfig.java

Enter gradlew bootRun in CMD to run the application. 

Go to localhost:8080/register to register and localhost:8080/login to login. 

If this application were to use JDBC type data store, and if we didn't have custom database schema, spring would have looked for the following tables and schema in a database:

    select username,password,enabled from users where username = ?;
    select username,authority from authorities where username = ?;
    select g.id, g.group_name, ga.authority from groups g, group_members gm, group_authorities ga where gm.username = ? and g.id = ga.group_id and g.id = gm.group_id;

Spring Security expects above tables and schema to be present in database. But if we had different tables and schema, there is a way to tell Spring Security to use that instead:

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
          .jdbcAuthentication()
          .dataSource(dataSource)
          .usersByUsernameQuery(
                "select username, password MySpecialTable " +
                "where username=?")
          .authoritiesByUsernameQuery(
                "select username, authority from MySecondSpecialTable " +
                "where username=?");
    }

Another way is to use custom UserDetailsService. In it, implement UserDetailsService and overload UserByUsername method like below 

    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user != null) {
			return user;
		}
		throw new UsernameNotFoundException("User '" + username + "' not found");
	}

and inject custom UserDetailsService into a class that extends WebSecurityConfigurerAdapter and overrides configure(AuthenticationManagerBuilder auth) method like this:

    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
	}
    
This way, we can use custom JDBC tables/schemas, or just use totally different user store, like in this example, NoSQL database is used. 
