package com.example.androidfundamental.ui.fragment

//class HomeFragment : Fragment() {
//
//    private lateinit var binding: FragmentHomeBinding
//    private lateinit var eventViewModel: EventViewModel
//    private lateinit var upcomingAdapter: AdapterHomeUpcoming
//    private lateinit var finishedAdapter: AdapterHomeFinished
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentHomeBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val factory = ViewModelFactory(
//            application = requireActivity().application,
//            repository = Injection.provideEventRepository(),
//            owner = this
//        )
//        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]
//
//        setupRecyclerView()
//
//        // Fetch upcoming and finished events
//        if (!eventViewModel.hasFetchedUpcomingEvents) {
//            eventViewModel.fetchUpcomingEvents()
//        }
//
//        if (!eventViewModel.hasFetchedFinishedEvents) {
//            eventViewModel.fetchFinishedEvents()
//        }
//
//        setupAdapterClickListeners()
//        observeViewModel()
//    }
//
//    private fun setupRecyclerView() {
//        // Setup adapter for Upcoming Events
//        upcomingAdapter = AdapterHomeUpcoming()
//        binding.rvUpcomingHome.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        binding.rvUpcomingHome.adapter = upcomingAdapter
//
//        // Setup adapter for Finished Events
//        finishedAdapter = AdapterHomeFinished()
//        binding.rvFinishedHome.layoutManager = LinearLayoutManager(context)
//        binding.rvFinishedHome.adapter = finishedAdapter
//    }
//
//    private fun setupAdapterClickListeners() {
//
//        upcomingAdapter.setOnItemClickListenerhome { data ->
//            navigateToDetailFragment(data.id)
//        }
//
//
//        finishedAdapter.setOnItemClickListener { data ->
//            navigateToDetailFragment(data.id)
//        }
//    }
//
//    private fun navigateToDetailFragment(eventId: Int?) {
//        val bundle = Bundle().apply {
//            putInt("event_id", eventId ?: 0)
//        }
//        findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
//    }
//
//    private fun observeViewModel() {
//
//        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
//            when (result) {
//                is NetworkResult.Loading -> {
//                    upcomingAdapter.showShimmerEffect()
//                }
//
//                is NetworkResult.Success -> {
//                    upcomingAdapter.hideShimmerEffect()
//                    result.data?.let { events ->
//                        if (events.isNotEmpty()) {
//                            upcomingAdapter.submitList(events)
//                        } else {
//                            Toast.makeText(context, "No upcoming events found", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                }
//
//                is NetworkResult.Error -> {
//                    upcomingAdapter.hideShimmerEffect()
//                    Toast.makeText(
//                        context,
//                        result.message ?: "Error loading upcoming events",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//
//        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
//            when (result) {
//                is NetworkResult.Loading -> {
//                    finishedAdapter.showShimmerEffect()
//                }
//
//                is NetworkResult.Success -> {
//                    finishedAdapter.hideShimmerEffect()
//                    result.data?.let { events ->
//                        if (events.isNotEmpty()) {
//                            val limitedEvents = if (events.size > 5) events.subList(0,5) else events
//                            finishedAdapter.submitList(limitedEvents)
//                        } else {
//                            Toast.makeText(context, "No finished events found", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                }
//
//                is NetworkResult.Error -> {
//                    finishedAdapter.hideShimmerEffect()
//                    Toast.makeText(
//                        context,
//                        result.message ?: "Error loading finished events",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//}