# HomeAway Coding Challenge

An application integrating with FourSquareAPI to allow users to search for venues, see the list of venues on a map and
view venue details for a given venue.

## Application Architecture

There are five modules that are included as part of the application architecture. I will break each of
these down below.

### Model

The Model Layer only contains the raw model that reflects the responses that come from the Network layer. This layer will only be data
classes that are shared with parent project layers. Given the overall simplicity of this layer, there is no need to implement unit
tests as there is no business logic.

### Network

The Network layer will contain all network interaction integration with the FourSquare Venue Search and Venue Lookup APIs. I utilized
retrofit as an HTTP Client when interfacing with external networks. The interface utilized to generate the Retrofit API is called
FourSquareAPI. I elected to utilize RXJava in conjunction with Retrofit so I can easily manage these tasks in other modules.
Since there is some additional parsing that can be done on the objects returned from the FourSquare service before passing elements to
calling classes, I have also included a FourSquareManager class that will strip these unnecessary wrapper classes from network
responses. This class could also be utilized to perform any additional business logic needed before returning a Single to the UI. In the
network module, there is also dependency injection required to build the FourSquareManager as well as provide it later on. In order to
have dagger correctly build the application graph, we must include NetworkModule as a module in the final Component class.

#### Network Testing

The Networking layer has testing of both the FourSquareAPI and FourSquareManager. When testing the FourSquare API, we utilize a MockWebServer
on a custom instance of our FourSquareAPI so all traffic will work through this server. To queue responses, we use a TestUtil class that
opens files that contain Json that directly correlates to the Json returned from FourSquare. We are able to validate the content returned from
the FourSquare service here as well as make sure our integration stays consistent by testing the Query Parameters, base URL and request types.

#### Testing Dependency from Other Classes

The Network layer also provides a custom implementation of the FourSquareAPI that will be pointed to "localhost://8080". This will be utilized
by our Espresso testing suite that I will go into further detail on when talking through the app layer. There is an additional create() method
on the FourSquareAPI that allows us to create the Four Square API with a custom URL. To ensure that dagger creates a graph that contains the
FourSquareAPI pointed to local host, an implementing component must include the MockNetworkModule.


### ViewModel

The ViewModel layer will contain all business logic needed to display view components in the application. We will utilize LiveData from Google's architecture
components library to notify views of updated components from any asynchronous actions, whether it be a network call or a database action. Each ViewModel will
extend the BaseViewModel. The BaseViewModel will manage disposable subscriptions to prevent memory leaks. In the ViewModel layer, we also map a Venue into a
DisplayVenue so that we only share elements needed by the UI and don't have to add any addtional business logic to our Fragment/Activity classes. The
View Model layer also provides a module to be included into the base Dagger component. We bind all View Models using a custom key, @ViewModelKey so that we
can utilize the map created by Dagger to inject the ViewModelFactory.

#### VenueDetailViewModel

The VenueDetailViewModel will contain any interaction needed to support the VenueDetail Screen. This View Model will be interact with the FourSquareAPI
when loading venue details. Upon loading VenueDetails, it will update the live data object containing a venue with a new venue that has a Venue's URL

#### VenueListViewModel

The VenueListViewModel will handle loading a list of venues from a given search term, updating whether or not an item is favorited in the VenueDatabase
and building a GoogleMapsUrl for a given venue. Upon successfully receiving a list of venues from the FourSquareApi, we will query the venue database to
see if any of the venues provided from four square have been previously favorited by the user. Upon receiving this list, we will merge the results and
create a list of DisplayVenues that will be used to populate a list of venues sent to the app layer. Upon successfully merging the results of our database
query and foursquare api lookup, we synchronously load a corresponding GoogleMaps static image to cache that will be displayed on the VenueDetails page.
When a user taps on the favorite icon, we call the venue database and either insert or remove the item into the VenueDatabase depending on whether or not
the venue was previously favorited.

#### VenueSharedViewModel

This ViewModel will be utilized to share values between fragments; it is intended to be used at the activity level.

#### ViewModel Testing

The VenueDetail and VenueList ViewModels have corresponding unit tests that test the loading a Venue List, loading a given venue's details and building the
Static Maps Image. Each test will extend a Base Test so we can override RXSchedulers to use a custom TestScheduler to ensure Actions from RX are scheduled
appropriately. When testing the view models, we mock the database and foursquareapi implementations and return built responses so we are only testing the
viewmodel components in these tests.

### Database

The database layer will contain all interactions with the database that is built for persisting favorited venue's across sessions. I chose to utilize
Room for interfacing with local databases. The VenueDao class is the main interface utilized when interacting with Room. This class is utilized to build
our VenueDatabase that will only be a list of Venue Ids. It will handle inserting, removing and querying the database. Similar to the network layer, there
is a class, VenueDatabaseManager, that will handle any additional business logic needed between the database layer and the implementing layer. The database
layer also requires injection and includes a DatabaseModule that will provide the VenueDatabaseManager and the VenueDao.

#### Database Testing

The Database layer is tested with VenueDatabaseManagerTest and VenueDatabaseTest. The VenueDatabaseTest will test the VenueDao implementation utilizing the
Room testing library so we can execute Database queries on the main thread. It will execute remove, insert and query and then validate the contents of the
database after testing these actions. In the VenueDatabaseManagerTest, we will mock the VenueDao object and stub responses from the DAO.

### App

The App layer contains all application components that will be displayed to the user. The app layer has a simple VenueActivity which serves
as the lone activity for the application. The VenueActivity contains the shared view model and shares it to all three fragments. We utilize
DaggerAndroid to handle injection of all activities and fragments and build our Dagger graph using the Application Component which we inject
in the CodingApplication class. There is a package, homeaway.com.challenge.animation, which handles a custom animation of the floating
action button exploding into a new view and collapsing into a previous view. The app also includes to kotlin-android-extensions in the Parcelize
annotation and dynamic view binding. The parcelize annotation essentially implements the Parcelable interface for you while dynamic view binding
will automatically bind views by id and layout file. I utilized both to cut down on the number of lines of code I had to write.

#### VenueSearchFragment

The Venue Search fragment is a simple search fragment that will allow the user to perform a typeahead search on a searchview that will display
search results in a RecyclerView to the user. If there is more than one result returned, the user sees a FloatingActionButton that upon tapping
will show all of the Venue's in the Recycler View on a Google Map. On this screen, a user also has the ability to mark a venue as a favorite
venue that will persist across launches of the application.

#### VenueDetailFragment

The Venue Detail Fragment is a fragment that contains a CollapsingToolbar that contains an ImageView that will display a (hopefully cached) Google
Maps static image. The content on the rest of the page contains a link that will drive the user to the venue's webpage externally using an explicit
intent and will also show if the current venue is a favorite venue.

#### VenueMapListFragment

The VenueMapsListFragment shows a list of venues from the shared view model on a Google Map. Each marker on the map will show an InfoWindow when
tapped that will have the Venue Name in the window. Upon tapping an info window, a user will be navigated to the VenueDetailFragment. We pass
FAB view parameters through the bundle when instantiating this fragment so that we can collapse this fragment back into the fab on the search page.

#### EspressoTesting

There is a small Espresso testing suite for VenueSearch and VenueDetail. This suite will be built pointing to the mock variant of the application.
The reason we do this is so wec an include the correct module when providing the FourSqaureAPI mock implementation. The MockWebServer utilizes
a Dispatcher, MockDispatcher, to handle mapping HTTP Requests to mocked responses. Using a mockwebserver takes network availability,
inconsistency, and altered data out of the question. It greatly speeds up our Espresso tests as well as response times are almost instantaneous.

## Building the Application

To build the application from the command line, you need to spin up an instance of the gradle wrapper:

gradle wrapper --gradle-version 4.6

### Debug/Release versions of the .apk

If you unit test from the command line, we need to build a gradle wrapper and then build the preferred
variant. The release variant is signed and can be installed via command line.

You can do this using the following commands:

 ./gradlew clean assembleRelease or ./gradlew clean assembleDebug

You can also build these variants from AndroidStudio in the gradle tool window with assembleRelease
or assembleDebug


### UnitTesting

If you unit test from the command line, we need to build a gradle wrapper and then run the testing suite.
The release version and debug version have different package names so they can be installed alongside
one another on the same device.
You can do this using the following commands:

 ./gradlew clean testDebugUnitTest

On the contrary, you can also run any test individually or at a class level by highlighting the
method or class and selecting run, or you can execute the whole suite using the Gradle Commands window.

### Espresso

The Espresso build of this application relies on the mock variant. Utilizing the mock variant allows
us to make use of MockWebServer. You can build and run the Espresso suite with the following command:

./gradlew clean connectedAndroidTest

If you would like to run Espresso from Android Studio, there is some configuration that needs to be
done to swtich between building the main application and building the espresso applicaiton. Since the
Espresso variant relies on the 'mock' build variant, you must change the build variant from the default,
which is debug, to mock. If you would like to switch back, you must change the selected variant back to
either debug or release. More information can be found at the following link:
https://stackoverflow.com/questions/44368130/android-testbuildtype-not-working