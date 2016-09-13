(defproject commiteth "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[metosin/compojure-api "1.1.6"]
                 [re-frame "0.8.0"]
                 [cljs-ajax "0.5.8"]
                 [secretary "1.2.3"]
                 [reagent-utils "0.2.0"]
                 [reagent "0.6.0-rc"]
                 [org.clojure/clojurescript "1.9.225" :scope "provided"]
                 [org.clojure/clojure "1.8.0"]
                 [selmer "1.0.7"]
                 [markdown-clj "0.9.89"]
                 [ring-middleware-format "0.7.0"]
                 [metosin/ring-http-response "0.8.0"]
                 [bouncer "1.0.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.3"]
                 [org.webjars/font-awesome "4.6.3"]
                 [org.webjars/bootstrap-social "5.0.0"]
                 [org.webjars.bower/tether "1.3.3"]
                 [org.clojure/tools.logging "0.3.1"]
                 [compojure "1.5.1"]
                 [http-kit "2.1.18"]
                 [ring/ring-json "0.4.0"]
                 [ring-webjars "0.1.1"]
                 [ring/ring-defaults "0.2.1"]
                 [mount "0.1.10"]
                 [cprop "0.1.9"]
                 [org.clojure/tools.cli "0.3.5"]
                 [luminus-nrepl "0.1.4"]
                 [buddy "1.0.0"]
                 [buddy/buddy-auth "1.1.0"]
                 [luminus-migrations "0.2.6"]
                 [conman "0.6.0"]
                 [org.postgresql/postgresql "9.4.1209"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [luminus-immutant "0.2.2"]
                 [overtone/at-at "1.2.0"]
                 [clj.qrgen "0.4.0"]
                 [digest "1.4.4"]
                 [tentacles "0.5.1"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main commiteth.core
  :migratus {:store :database :db ~(get (System/getenv) "DATABASE_URL")}

  :plugins [[lein-cprop "1.0.1"]
            [migratus-lein "0.4.1"]
            [lein-cljsbuild "1.1.3"]
            [lein-immutant "2.1.0"]
            [lein-sassc "0.10.4"]
            [lein-auto "0.1.2"]]
  :immutant {
    :war {
        :name "ROOT"
        :destination "/opt/wildfly/standalone/deployments"
        :context-path "/"
    }
  }

  :sassc
  [{:src         "resources/scss/screen.scss"
    :output-to   "resources/public/css/screen.css"
    :style       "nested"
    :import-path "resources/scss"}]

  :auto
  {"sassc" {:file-pattern #"\.(scss|sass)$" :paths ["resources/scss"]}}

  :clean-targets ^{:protect false}
[:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port       7002
   :css-dirs         ["resources/public/css"]
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}


  :profiles
  {:uberjar       {:omit-source    true
                   :prep-tasks     ["compile" ["cljsbuild" "once" "min"]]
                   :cljsbuild
                                   {:builds
                                    {:min
                                     {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                                      :compiler
                                                    {:output-to     "target/cljsbuild/public/js/app.js"
                                                     :externs       ["react/externs/react.js"]
                                                     :optimizations :advanced
                                                     :pretty-print  false
                                                     :closure-warnings
                                                                    {:externs-validation :off :non-standard-jsdoc :off}}}}}


                   :aot            :all
                   :uberjar-name   "commiteth.jar"
                   :source-paths   ["env/prod/clj"]
                   :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]

   :project/dev   {:dependencies   [[prone "1.1.1"]
                                    [ring/ring-mock "0.3.0"]
                                    [ring/ring-devel "1.5.0"]
                                    [pjstadig/humane-test-output "0.8.1"]
                                    [doo "0.1.7"]
                                    [binaryage/devtools "0.8.1"]
                                    [figwheel-sidecar "0.5.4-7"]
                                    [com.cemerick/piggieback "0.2.2-SNAPSHOT"]]
                   :plugins        [[com.jakemccrary/lein-test-refresh "0.14.0"]
                                    [lein-doo "0.1.7"]
                                    [lein-figwheel "0.5.4-7"]
                                    [org.clojure/clojurescript "1.9.225"]]
                   :cljsbuild
                                   {:builds
                                    {:app
                                     {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                                      :compiler
                                                    {:main          "commiteth.app"
                                                     :asset-path    "/js/out"
                                                     :output-to     "target/cljsbuild/public/js/app.js"
                                                     :output-dir    "target/cljsbuild/public/js/out"
                                                     :source-map    true
                                                     :optimizations :none
                                                     :pretty-print  true}}}}



                   :doo            {:build "test"}
                   :source-paths   ["env/dev/clj" "test/clj"]
                   :resource-paths ["env/dev/resources"]
                   :repl-options   {:init-ns user}
                   :injections     [(require 'pjstadig.humane-test-output)
                                    (pjstadig.humane-test-output/activate!)]}
   :project/test  {:resource-paths ["env/dev/resources" "env/test/resources"]
                   :cljsbuild
                                   {:builds
                                    {:test
                                     {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                                      :compiler
                                                    {:output-to     "target/test.js"
                                                     :main          "commiteth.doo-runner"
                                                     :optimizations :whitespace
                                                     :pretty-print  true}}}}

                   }
   :profiles/dev  {}
   :profiles/test {}})
