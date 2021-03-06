# Default installation directories.
set(SOLARUS_LIBRARY_INSTALL_DESTINATION "lib" CACHE PATH "Library install destination")
set(SOLARUS_EXECUTABLE_INSTALL_DESTINATION "bin" CACHE PATH "Binary install destination")
set(SOLARUS_HEADERS_INSTALL_DESTINATION "include" CACHE PATH "Headers install destination")

# Files to install with make install.
if(SOLARUS_BUNDLE)
  # Install the bundle if requested.
  install(TARGETS solarus_run
    BUNDLE DESTINATION ${SOLARUS_EXECUTABLE_INSTALL_DESTINATION}
  )
else()
  # Install the shared library and the solarus_run executable.
  install(TARGETS solarus solarus_run
    LIBRARY DESTINATION ${SOLARUS_LIBRARY_INSTALL_DESTINATION}
    RUNTIME DESTINATION ${SOLARUS_EXECUTABLE_INSTALL_DESTINATION}
  )
  # Install headers: useful for projects that use Solarus as a library.
  install(DIRECTORY
    "${CMAKE_BINARY_DIR}/include/solarus"  # For config.h.
    "${SOLARUS_ENGINE_SOURCE_DIR}/include/solarus"
    DESTINATION ${SOLARUS_HEADERS_INSTALL_DESTINATION}
  )
endif()

