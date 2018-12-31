const LIVERELOAD_PORT = 36729;

const serveStatic = require('serve-static');
const proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;
const livereloadSnippet = require('connect-livereload')({ port: LIVERELOAD_PORT });

const yeomanConfig = {
  app: '../src/main/app',
  dist: '../target/yo/dist',
  test: '../src/test/js'
};

const tsconfig = require(yeomanConfig.app + '/tsconfig.json');
const tsconfigTest = require(yeomanConfig.test + '/tsconfig.json');

module.exports = grunt => {
  require('time-grunt')(grunt);
  require('load-grunt-tasks')(grunt);

  grunt.initConfig({
    yeoman: yeomanConfig,
    ts: {
      tests: {
        files: [{
          src: tsconfigTest.files.map(file => yeomanConfig.test + '/' + file),
          dest: yeomanConfig.test,
          options: {
            module: "commonjs",
            target: "es3",
            lib: ["es2015", "dom", "es5"],
            alwaysStrict: true
          }
        }]
      },
      sources: {
        files: [{
          src: tsconfig.files.map(file => yeomanConfig.app + '/' + file),
          dest: yeomanConfig.app,
        }]
      },
      options: {
        module: "commonjs",
        target: "es3",
        lib: ["es2015", "dom", "es5"],
        alwaysStrict: true
      }
    },
    focus: {
      dev: {
        exclude: ['test']
      }
    },
    watch: {
      css: {
        files: ['<%= yeoman.app %>/styles/{,*/}*.scss'],
        tasks: ['sass', 'copy:fonts', 'autoprefixer']
      },
      test: {
        files: ['<%= yeoman.app %>/**/*.{ts,html}', '<%= yeoman.test %>/**/*.ts','!<%= yeoman.app %>/bower_components/**'],
        tasks: ['ts:tests', 'karma:unit', 'regex-check']
      },
      livereload: {
        options: {
          livereload: LIVERELOAD_PORT,
          open: false
        },
        tasks: ['ts:sources'],
        files: [
          '<%= yeoman.app %>/**/*.{html,ts}',
          '!<%= yeoman.app %>/bower_components/**',
          '.tmp/styles/**/*.css',
          '{.tmp,<%= yeoman.app %>}/scripts/**/*.ts',
          '<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
        ]
      }
    },
    autoprefixer: {
      options: ['last 1 version'],
      dist: {
        files: [{
          expand: true,
          cwd: '.tmp/styles/',
          src: '{,*/}*.css',
          dest: '.tmp/styles/'
        }]
      }
    },
    connect: {
      options: {
        port: 9010,
        hostname: '0.0.0.0',
        livereload: LIVERELOAD_PORT
      },
      proxies: [{
        context: '/eperusteet-ylops-service',
        host: 'localhost',
        port: 8080,
        https: false,
        changeOrigin: true,
        xforward: false
      },{
        context: '/virkailija-raamit',
        host: 'localhost',
        port: 8080,
        https: false,
        changeOrigin: true,
        xforward: false
      }],
      livereload: {
        options: {
          base: ['.tmp', '<%= yeoman.app %>'],
          middleware: connect => {
            return [
              proxySnippet,
              livereloadSnippet,
              serveStatic('.tmp'),
              serveStatic(yeomanConfig.app)
            ];
          }
        }
      },
      test: {
        options: {
          port: 0,
          middleware: connect => {
            return [
              serveStatic('.tmp'),
              serveStatic(yeomanConfig.test)
            ];
          }
        }
      },
      dist: {
        options: {
          middleware: connect => {
            return [
              serveStatic(yeomanConfig.dist)
            ];
          }
        }
      }
    },
    clean: {
      options: {
        force: true // files outside working directory!
      },
      dist: {
        files: [{
          dot: true,
          src: [
            '.tmp',
            '<%= yeoman.dist %>/*',
            '!<%= yeoman.dist %>/.git*'
          ]
        }]
      },
      server: '.tmp'
    },
    rev: {
      dist: {
        files: {
          src: [
            '<%= yeoman.dist %>/scripts/{,*/}*.js',
            '<%= yeoman.dist %>/styles/{,*/}*.css',
            '<%= yeoman.dist %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
            '<%= yeoman.dist %>/styles/fonts/*'
          ]
        }
      }
    },
    useminPrepare: {
      html: '<%= yeoman.app %>/index.html',
      options: {
        dest: '<%= yeoman.dist %>',
        flow: {
          html: {
            steps: {
              js: ['concat','uglify'],
              css: ['cssmin']
            },
            post: {}
          }
        }
      }
    },
    usemin: {
      html: ['<%= yeoman.dist %>/*.html','<%= yeoman.dist %>/views/**/*.html'],
      css: ['<%= yeoman.dist %>/styles/{,*/}*.css'],
      js: [
        '<%= yeoman.dist %>/scripts/*.scripts.js',
        '<%= yeoman.dist %>/scripts/*.templates.js'
      ],
      options: {
        assetsDirs: ['<%= yeoman.dist %>','<%=yeoman.dist %>/styles'],
        patterns: {
          js: [
          [/\\?"(images\/.*?\.(png|gif|jpg|jpeg|svg))\\?"/g,'JS rev png images']
          ]
        }
      }
    },
    imagemin: {
      dynamic: {
        files: [{
          expand: true,
          cwd: '<%= yeoman.app %>/images',
          src: '**/*.{png,jpg,jpeg,svg}',
          dest: '<%= yeoman.dist %>/images'
        }]
      }
    },
    cssmin: {},
    htmlmin: {
      dist: {
        options: {},
        files: [{
          expand: true,
          cwd: '<%= yeoman.app %>',
          src: ['*.html'],
          dest: '<%= yeoman.dist %>'
        }]
      }
    },
    copy: {
      dist: {
        files: [{
          expand: true,
          dot: true,
          cwd: '<%= yeoman.app %>',
          dest: '<%= yeoman.dist %>',
          src: [
            '*.{ico,png,txt}',
            '.htaccess',
            'images/{,*/}*.{gif,webp}',
            'styles/fonts/*'
          ]
        }, {
          expand: true,
          cwd: '<%= yeoman.app %>/bower_components/ckeditor',
          dest: '<%= yeoman.dist %>/bower_components/ckeditor',
          src: [
            '**',
            '!samples/**'
          ]
        }, {
            expand: true,
            cwd: '<%= yeoman.app %>/bower_components/ng-file-upload',
            dest: '<%= yeoman.dist %>/bower_components/ng-file-upload',
            src: [
                'FileAPI.flash.swf',
                'FileAPI.min.js'
            ]
        }, {
          expand: true,
          cwd: '<%= yeoman.app %>/localisation',
          dest: '<%= yeoman.dist %>/localisation',
          src: [
            '*.json'
          ]
        }, {
          expand: true,
          cwd: '<%= yeoman.app %>/ckeditor-plugins',
          dest: '<%= yeoman.dist %>/ckeditor-plugins',
          src: [
            '**'
          ]
        }, {
          expand: true,
          cwd: '.tmp/images',
          dest: '<%= yeoman.dist %>/images',
          src: [
            'generated/*'
          ]
        }, {
          expand: true,
          cwd: '<%= yeoman.app %>/bower_components/bootstrap-sass-official/assets/fonts/bootstrap',
          dest: '<%= yeoman.dist %>/styles/fonts',
          src: '*.{eot,svg,ttf,woff,woff2}'
        }]
      },
      fonts: {
        expand: true,
        cwd: '<%= yeoman.app %>/bower_components/bootstrap-sass-official/assets/fonts/bootstrap',
        dest: '.tmp/styles/fonts/',
        src: '*.{eot,svg,ttf,woff,woff2}'
      }
    },
    concurrent: {
      server: [
        'sass'
      ],
      test: [
        'sass'
      ],
      dist: [
        'sass',
        'imagemin',
        'htmlmin'
      ]
    },
    karma: {
      unit: {
        configFile: 'karma.conf.js',
        singleRun: true
      }
    },
    cdnify: {
      dist: {
        html: ['<%= yeoman.dist %>/*.html']
      }
    },
    uglify: {
      options: { mangle: false },
      dist: {
        files: {
          '.tmp/concat/scripts/scripts.js': [
            '<%= yeoman.dist %>/scripts/scripts.js'
          ]
        }
      }
    },
    sass: {
      dist: {
        files: {
          '.tmp/styles/eperusteet-ylops.css': '<%= yeoman.app %>/styles/eperusteet-ylops.scss'
        }
      }
    },
    ngtemplates: {
      dist: {
        cwd: '<%= yeoman.app %>',
        src: 'views/**/*.html',
        dest: '.tmp/views.js',
        append: true,
        options:    {
          module: 'ylopsApp',
          usemin: 'scripts/scripts.js',
          htmlmin: { collapseWhitespace: true, removeComments: true }
        }
      }
    },
    'regex-check': {
      templateurls: {
        files: [{src: ['<%= yeoman.app %>/scripts/**/*.js']}],
        options: {
          /* Check that templateUrls don't start with slash */
          pattern : /templateUrl:\s*['"]\//m
        }
      },
      showhide: {
        files: [{src: ['<%= yeoman.app %>/{scripts,views}/**/*.{js,html}']}],
        options: {
          /* Check that ng-show/ng-hide are not used in same element */
          pattern : /(ng-show=|ng-hide=)[^>]+(ng-hide=|ng-show=)/m
        }
      },
      controllerNaming: {
        files: [{src: ['<%= yeoman.app %>/scripts/**/*.js']}],
        options: {
          /* Enforce CamelCaseController naming */
          pattern : /\.controller\s*\(\s*'([a-z][^']+|([^'](?!Controller))+)'/g
        }
      }
    }
  });

  grunt.registerTask('dev', [
    'clean:server',
    'ts:sources',
    'concurrent:server',
    'copy:fonts',
    'autoprefixer',
    'configureProxies',
    'connect:livereload',
    'focus:dev'
  ]);

  grunt.registerTask('test', [
    'clean:server',
    'ts:tests',
    'copy:fonts',
    'concurrent:test',
    'autoprefixer',
    'regex-check',
    'karma'
  ]);

  grunt.registerTask('build', [
    'clean:dist',
    'ts:sources',
    'useminPrepare',
    'concurrent:dist',
    'autoprefixer',
    'ngtemplates',
    'concat',
    'copy:dist',
    'cssmin',
    'uglify',
    'rev',
    'usemin'
  ]);

  grunt.registerTask('default', [
    'test',
    'build'
  ]);
};
