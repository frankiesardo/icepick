(defproject frankiesardo/icepick "3.0.2"
  :description "Android instance state made easy"
  :url "https://github.com/frankiesardo/icepick"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :java-source-paths ["src"]
  :javac-options ["-target" "1.6" "-source" "1.6"]
  :deploy-repositories {"snapshots" {:url "https://clojars.org/repo"
                                     :username [:gpg :env/clojars_username]
                                     :password [:gpg :env/clojars_password]}
                        "releases" {:url "https://clojars.org/repo"}}
  :profiles {:provided {:dependencies [[com.google.android/android "4.1.1.4"]]}
             :dev {:dependencies [[org.clojure/clojure "1.6.0"]]}})
